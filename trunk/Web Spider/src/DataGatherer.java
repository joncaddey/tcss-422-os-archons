import java.util.ArrayList;
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

	private int my_total_pages;
	private int my_total_words;
	private int my_total_links;
	private int my_pages_retrieved;
	private Map<String, Integer> my_frequencies;
	private ConsoleReporter my_reporter;
	private final List<String> my_original_keywords;

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

	public void process(final Data the_data) {
		my_pages_retrieved++;
		my_total_words += the_data.getWords();
		for (String s : my_frequencies.keySet()) {
			my_frequencies.put(s, my_frequencies.get(s)
					+ the_data.getFrequencies().get(s));
		}
		my_total_links += the_data.getURLs().size();
		
		my_reporter.report(the_data);

	}

	public void reportData() {
		// Parsed: www.tacoma.washington.edu/calendar/
		// Pages Retrieved: 12
		// Average words per page: 321
		// Average URLs per page: 11
		// Keyword Ave. hits per page Total hits
		// albatross 0.001 3
		// carrots 0.01 5
		// everywhere 1.23 19
		// etc..........
		//
		// intelligence 0.000 0
		//
		// Page limit: 5000
		// Average parse time per page .001msec
		// Total running time: 0.96 sec
	}

}
