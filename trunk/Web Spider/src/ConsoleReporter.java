/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * Nov 5, 2011
 */

import java.net.URL;
import java.util.Map;

/**
 * Used for displaying results of page visits to console.  Repeated crawls starting at the same
 * seed are trials, and results of trials can be reported.
 * 
 * @author Travis Jensen
 * @author Jonathan Caddey
 * @version 1.0
 */
public class ConsoleReporter {
	
	private static final int NANOS_IN_SECOND = 1000000000;
	
	private static final String BORDER = "==============================================================";

	
	/**
	 * Used to display the results of a single page visit.
	 * @param the_url
	 * @param the_page_num the number of this page in the order visited.
	 * @param the_page_limit the total pages to be visited.
	 * @param the_words total words.
	 * @param the_links total links.
	 * @param the_frequencies keyword frequency pairs.
	 * @param the_running_time time spent crawling so far.
	 */
	public void report(final URL the_url, final int the_page_num,
			final int the_page_limit, final int the_words, final int the_links,
			final Map<String, Integer> the_frequencies,
			final long the_running_time) {

		System.out.println("Parsed: " + the_url);
		System.out.println("Pages Retrieved: " + the_page_num);
		System.out.printf("Average words per page: %d", the_words
				/ the_page_num);
		System.out.println();
		System.out.printf("Average links per page: %d", the_links
				/ the_page_num);
		System.out.println();
		System.out
				.println("Keyword               Ave. hits per page       Total hits");
		for (String s : the_frequencies.keySet()) {
			int hits = the_frequencies.get(s);
			System.out.printf("  %-24s%-10.4f%17d", s, (float) hits
					/ the_page_num, hits);
			System.out.println();
		}

		System.out.println("Page limit: " + the_page_limit);
		System.out.printf("Avg parse time per page: %.3f sec",
				(double) the_running_time / the_page_num / NANOS_IN_SECOND);
		System.out.println();
		System.out.printf("Total running time: %.3f sec",
				(double) the_running_time / NANOS_IN_SECOND);
		System.out.println();
		System.out.println();
	}

	/**
	 * Called at the beginning of a trial.
	 * @param the_trial_num the the position of this trial.
	 * @param the_trial_limit the total trials to be done.
	 */
	public void reportStart(final int the_trial_num, final int the_trial_limit) {
		if (the_trial_limit > 1) {
			System.out.println(BORDER);
			System.out.println();
			System.out.println("Starting trial " + the_trial_num);
			System.out.println();
			System.out.println(BORDER);
			System.out.println();
		}
	}
	
	/**
	 * Called to report the results of a trial. Displays the total times for
	 * each trail so far as well as the average time.
	 * 
	 * @param the_trial
	 *            the position of this trial.
	 * @param the_trial_limit
	 *            the total trials to be done.
	 * @param the_times
	 *            the times of each trial so far.
	 */
	public void reportSummary(final int the_trial, final int the_trial_limit,
			final long[] the_times) {
		if (the_trial_limit > 1) {
			System.out.println();
			System.out.println("Trial " + the_trial + " of " + the_trial_limit
					+ " complete!");
			long sum = 0;
			System.out.println("Trial\tTotal time");
			for (int i = 0; i < the_times.length; i++) {
				sum += the_times[i];
				System.out.printf("%d\t%.3f", (i + 1),
						nanoToSecond(the_times[i]));
				System.out.println();
			}
			System.out.printf("Avg time: %.3f sec", nanoToSecond(sum)
					/ the_times.length);
			System.out.println();
			System.out.println();
			System.out.println();
		}
	}


	/**
	 * Called when all trials are finished.
	 */
	public void reportEnd() {
		System.out.println("Shutting down *Beeeowp*");
	}
	
	/**
	 * @param the_nano a number of nanoseconds.
	 * @return an equivalent fraction of seconds.
	 */
	private double nanoToSecond(final long the_nano) {
		return (double) the_nano / NANOS_IN_SECOND;
	}
}
