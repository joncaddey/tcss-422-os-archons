import java.net.URL;
import java.util.Map;

/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * Nov 5, 2011
 */

/**
 * @author Travis Jensen
 * @author Jonathan Caddey
 * @version 1.0
 */
public class ConsoleReporter {
	private static final int MILLIS_IN_SECOND = 1000;
	private static final String BORDER = "==============================================================";
	
	public void report(final URL the_url, final int the_page_num,
			final int the_page_limit, final int the_words, final int the_links,
			final Map<String, Integer> the_frequencies,
			final int the_running_time) {
		/*
		 * 
		 * 
		 * Parsed: www.tacoma.washington.edu/calendar/ Pages Retrieved: 12
		 * Average words per page: 321 Average URLs per page: 11 Keyword Ave.
		 * hits per page Total hits albatross 0.001 3 carrots 0.01 5 everywhere
		 * 1.23 19 etc..........
		 * 
		 * intelligence 0.000 0
		 * 
		 * Page limit: 5000 Average parse time per page .001msec Total running
		 * time: 0.96 sec
		 */
		
		System.out.println("Parsed: " + the_url);
		System.out.println("Pages Retrieved: " + the_page_num);
		System.out.printf("Average words per page: %d", the_words / the_page_num);
		System.out.println();
		System.out.printf("Average links per page: %d", the_links / the_page_num);
		System.out.println();
		System.out.println("Keyword               Ave. hits per page       Total hits");
		for (String s : the_frequencies.keySet()) {
			int hits = the_frequencies.get(s);
			System.out.printf("  %-24s%-10.4f%17d", s, (float) hits / the_page_num, hits);
			System.out.println();
		}
		
		
		
		System.out.println("Page limit: " + the_page_limit);
		System.out.printf("Avg parse time per page: %.3f sec", (double) the_running_time / the_page_num / MILLIS_IN_SECOND);
		System.out.println();
		System.out.printf("Total running time: %.3f sec", (double) the_running_time / MILLIS_IN_SECOND);
		System.out.println();
		System.out.println();
	}
	
	public void reportSummary(final int the_trial, final int the_trial_limit, final int the_total_time) {
		System.out.println();
		System.out.println("Trial " + the_trial + " of " + the_trial_limit + " complete!");
		System.out.printf("Avg time: %.3f sec", (double) the_total_time / the_trial / MILLIS_IN_SECOND);
		System.out.println();
		System.out.println();
		System.out.println();
	}

	public void reportStart(final int trial_num) {
		System.out.println(BORDER);
		System.out.println();
		System.out.println("Starting trial " + trial_num);
		System.out.println();
		System.out.println(BORDER);
		System.out.println();
	}
}
