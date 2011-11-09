import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
public class DataGatherer {

	private static final int NANOS_IN_MILLI = 1000000;
	private int my_total_words;
	private int my_total_links;
	private int my_pages_retrieved;
	private Map<String, Integer> my_frequencies;
	private ConsoleReporter my_reporter;
	private final List<String> my_original_keywords;
	private long my_start_time;

	/**
	 * Crawl responsibly--know your page limit.
	 */
	private final int my_page_limit;

	public DataGatherer(final List<String> the_keywords,
			final int the_page_limit, ConsoleReporter the_reporter) {
		my_original_keywords = new ArrayList<String>(the_keywords);
		my_reporter = the_reporter;
		my_page_limit = the_page_limit;
		my_frequencies = new HashMap<String, Integer>();
		for (String s : the_keywords) {
			my_frequencies.put(s, 0);
		}

	}

	public Set<String> getKeywords() {
		return my_frequencies.keySet();
	}

	public int getPageLimit() {
		return my_page_limit;
	}

	public void process(final URL the_url, final int the_words, final int the_links, final Map<String, Integer> the_frequencies) {
		my_pages_retrieved++;
		my_total_words += the_words;
		for (String s : my_frequencies.keySet()) {
			my_frequencies.put(s, my_frequencies.get(s)
					+ the_frequencies.get(s));
		}
		my_total_links += the_links;

		my_reporter.report(the_url, my_pages_retrieved, my_page_limit,
				my_total_words, my_total_links,
				Collections.unmodifiableMap(my_frequencies),
				(int) ((System.nanoTime() - my_start_time) / NANOS_IN_MILLI));

	}
	
	public void startTrial() {
		my_start_time = System.nanoTime();
	}
	
	
		

}
