/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * 2011-10-23
 */

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;


/**
 * Collects data for a web crawl.  A crawl may be repeated from a given seed multiple times, each of which is called
 * a trial. Generic attributes of a web page are recorded, as well as occurrences of specific keywords.
 * @author Jonathan Caddey
 * @author Travis Jensen
 * @version 1.0
 */
public class DataGatherer extends Observable {

	private long my_start_time;
	private int my_total_words;
	private int my_total_links;
	private int my_pages_retrieved;
	
	/**
	 * Pairings of keywords and their total occurrences.
	 */
	private Map<String, Integer> my_frequencies;
	private final ConsoleReporter my_reporter;
	private final List<String> my_original_keywords;
	
	/**
	 * The number of trials to gather data for.
	 */
	private int my_trial_limit;
	
	/**
	 * The current trial (0-indexed).
	 */
	private int my_trial_num;
	
	/**
	 * Whether a trial is in progress.
	 */
	private boolean my_running;
	
	/**
	 * The total times of each previously executed trial.
	 */
	private long[] my_total_times;

	/**
	 * Crawl responsibly--know your page limit.
	 */
	private final int my_page_limit;

	/**
	 * Creates a DataGatherer that will look for occurrences of the specified keywords as the Internet
	 * is crawled.  A DataGatherer should be given to a Controller to collect the data.
	 * @param the_keywords words to count the occurrences of.
	 * @param the_page_limit the number of pages to visit per trial.
	 * @param the_trials the number of times to repeat the crawl.
	 * @param the_reporter who can report the data.
	 */
	public DataGatherer(final List<String> the_keywords,
			final int the_page_limit, final int the_trials, final ConsoleReporter the_reporter) {
		my_original_keywords = new ArrayList<String>(the_keywords);
		my_reporter = the_reporter;
		my_page_limit = the_page_limit;
		my_trial_limit = the_trials;
		my_frequencies = new HashMap<String, Integer>();
		for (String s : the_keywords) {
			my_frequencies.put(s, 0);
		}
		my_total_times = new long[my_trial_limit];
	}
	
	/**
	 * @return the keywords whose occurrences I am counting.
	 */
	public Set<String> getKeywords() {
		return my_frequencies.keySet();
	}

	/**
	 * @return how many web pages I need data for per trial.
	 */
	public int getPageLimit() {
		return my_page_limit;
	}

	/**
	 * Process data parsed from a given URL.
	 * @param the_url the page visited.
	 * @param the_words the total words on the page.
	 * @param the_links the total links on the page.
	 * @param the_frequencies keyword occurrences pairs.
	 */
	public void process(final URL the_url, final int the_words, final int the_links, final Map<String, Integer> the_frequencies) {
		if (my_pages_retrieved < my_page_limit) {
			my_pages_retrieved++;
			my_total_words += the_words;
			for (String s : my_frequencies.keySet()) {
				my_frequencies.put(s, my_frequencies.get(s) + the_frequencies.get(s));
			}
			my_total_links += the_links;

			my_total_times[my_trial_num] = (long) (System.nanoTime() - my_start_time);
			my_reporter
					.report(the_url,
							my_pages_retrieved,
							my_page_limit,
							my_total_words,
							my_total_links,
							Collections.unmodifiableMap(my_frequencies),
							my_total_times[my_trial_num]);
		} else if (my_running) {
			// end of trial
			my_running = false;
			my_trial_num++;
			
			long[] total_times_so = new long[my_trial_num];
			System.arraycopy(my_total_times, 0, total_times_so, 0, my_trial_num);
			my_reporter.reportSummary(my_trial_num, my_trial_limit, total_times_so);
			if (!needsMoreData()) {
				my_reporter.reportEnd();
			}
			
			setChanged();
			notifyObservers(needsMoreData());
			
		}
	}

	/**
	 * Start the timer for a new trial.  If a trial is already in progress this invocation has no effect.
	 */
	public void startTrial() {
		if (!my_running && my_trial_num < my_trial_limit) {
			my_frequencies = new HashMap<String, Integer>();
			for (String s : my_original_keywords) {
				my_frequencies.put(s, 0);
			}
			my_total_words = 0;
			my_total_links = 0;
			my_pages_retrieved = 0;
			my_running = true;
			my_start_time = System.nanoTime();
			my_reporter.reportStart(my_trial_num + 1, my_trial_limit);
		}
	}
	
	/**
	 * @return whether I would like data for another trial.
	 */
	public boolean needsMoreData() {
		return my_trial_num < my_trial_limit;
	}
}
