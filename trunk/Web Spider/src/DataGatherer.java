import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * 2011-10-23
 */

/**
 * @author Jonathan Caddey
 * @author Travis Jensen
 * @version 1.0
 */
public class DataGatherer extends Observable {

	private static final int NANOS_IN_MILLI = 1000000;
	
	
	private long my_start_time;
	private int my_total_words;
	private int my_total_links;
	private int my_pages_retrieved;
	private Map<String, Integer> my_frequencies;
	private final ConsoleReporter my_reporter;
	private final List<String> my_original_keywords;
	private int my_trial_limit;
	private int my_trial_num;
	private boolean my_running;
	
	private int[] my_total_times;

	/**
	 * Crawl responsibly--know your page limit.
	 */
	private final int my_page_limit;

	public DataGatherer(final List<String> the_keywords,
			final int the_page_limit, final int the_trials, final ConsoleReporter the_reporter) {
		my_original_keywords = new ArrayList<String>(the_keywords);
		my_reporter = the_reporter;
		my_page_limit = the_page_limit;
		my_trial_limit = the_trials;
		my_frequencies = new HashMap<String, Integer>();
		for (String s : my_original_keywords) {
			my_frequencies.put(s, 0);
		}
		my_total_times = new int[my_trial_limit];
	}
	
	public Set<String> getKeywords() {
		return my_frequencies.keySet();
	}

	public int getPageLimit() {
		return my_page_limit;
	}

	public void process(final URL the_url, final int the_words, final int the_links, final Map<String, Integer> the_frequencies) {
		if (my_pages_retrieved < my_page_limit) {
			my_pages_retrieved++;
			my_total_words += the_words;
			for (String s : my_frequencies.keySet()) {
				my_frequencies.put(s, my_frequencies.get(s) + the_frequencies.get(s));
			}
			my_total_links += the_links;

			my_total_times[my_trial_num] = (int) ((System.nanoTime() - my_start_time) / NANOS_IN_MILLI);
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
			
			// take avg of first trials
			int sum = 0;
			for (int i = 0; i < my_trial_num; i++) {
				sum += my_total_times[i];
			}
			my_reporter.reportSummary(my_trial_num, my_trial_limit, sum);
			
			setChanged();
			notifyObservers(needsMoreData());
			
		}
	}

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
			my_reporter.reportStart(my_trial_num + 1);
		}
	}
	
	public boolean needsMoreData() {
		return my_trial_num < my_trial_limit;
	}
	
	
		

}
