/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * Nov 3, 2011
 */

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Observable;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;

/**
 * @author Travis Jensen
 * @author Jonathan Caddey
 * @version 1.0
 */
public class PageToRetrieve extends Observable {
	private static final Collection<String> EXTENSIONS;
	static {
		Collection<String> extensions = new HashSet<String>();
		extensions.add("HTML");
		extensions.add("HTML");
		extensions.add("TXT");
		EXTENSIONS = Collections.unmodifiableCollection(extensions);
	}
	
	private static final int DEFAULT_TIMEOUT = 50;

	private  final BlockingQueue<Runnable> my_queue;

	private ThreadPoolExecutor my_tpe;

	private final Collection<String> my_visited = new HashSet<String>();

	public PageToRetrieve(final int the_max_thread_count, URL[] ignore) {
		for (URL url : ignore) {
			my_visited.add(this.normalURL(url));
		}
		 my_queue = new LinkedBlockingQueue<Runnable>();
		my_tpe = new ThreadPoolExecutor(0,
				the_max_thread_count, Integer.MAX_VALUE, TimeUnit.MILLISECONDS, my_queue);
	}

	/**
	 * URLs with paths must end with html, htm, or txt. Otherwise they are
	 * silently not enqueued.
	 * 
	 * @param the_url
	 *            the URL to visit.
	 */
	// TODO TESTING COMPLETION DATE: 00-00-2011
	// TODO FINALIZED AND APPROVED DATE: 00-00-2011
	public void enqueue(final URL the_url) {
		String normal = normalURL(the_url);
		if (!my_visited.contains(normal) && isValidURL(the_url)) {
			my_visited.add(normal);
			my_tpe.execute(new PageRetriever(new Page(the_url)));
		}
	}

	private synchronized void sendBack(final Page the_page) {
		setChanged();
		notifyObservers(the_page);
	}

	/**
	 * @param the_url
	 *            a URL to normalize.
	 * @return a representation of the URL used for determining whether two URLs
	 *         are equal.
	 */
	// TESTING COMPLETION DATE: 10-30-2011
	// TODO FINALIZED AND APPROVED DATE: 00-00-2011
	protected static String normalURL(final URL the_url) {
		StringBuilder sb = new StringBuilder();
		sb.append(the_url.getProtocol());
		sb.append("://");
		sb.append(the_url.getHost());
		sb.append(the_url.getPath());
		return sb.toString();
	}

	/**
	 * Whether the given URL will be enqueued if it has not been visited.
	 * 
	 * @param the_url
	 *            a URL to potentially enqueue.
	 * @return whether the given url has no path or ends with htm, html, or txt.
	 */
	// TESTING COMPLETION DATE: 10-30-2011
	// TODO FINALIZED AND APPROVED DATE: 00-00-2011
	public boolean isValidURL(final URL the_url) {
		boolean valid = the_url.getProtocol().equals("http");
		final String path = the_url.getPath();
		if (path.length() != 0 && !path.endsWith("/")) {
			int period = path.lastIndexOf('.');
			if (period != -1) {
				final String ext = path.substring(period + 1, path.length())
						.toUpperCase();
				valid = EXTENSIONS.contains(ext);
			} else {
				valid = false;
			}
		}
		return valid;
	}

	private class PageRetriever implements Runnable {

		private Page my_page;

		public PageRetriever(final Page the_page) {
			my_page = the_page;
		}

		public void run() {
			StringBuilder sb = new StringBuilder();
			Scanner scanner;
			try {
				URLConnection connection = my_page.getURL().openConnection();
				connection.setConnectTimeout(DEFAULT_TIMEOUT);
				scanner = new Scanner(my_page.getURL().openStream());
				while (scanner.hasNextLine()) {
					sb.append(scanner.nextLine());
					sb.append('\n');
				}
				my_page.setMarkup(sb);
				sendBack(my_page);
			} catch (Exception e) {
				// TODO nothing

			}
			
			
		}

	}

	public void shutdown() {
		my_tpe.shutdown();
		my_queue.clear();
	}

	// public static void main (String args[]) throws Exception {
	// PageToRetrieve ptr = new PageToRetrieve(2);
	// ptr.enqueue(new URL("http://faculty.washington.edu/gmobus/"));
	// }
}
