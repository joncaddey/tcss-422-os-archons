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
	
	private static final int THREAD_LIFE = 5000;
	
	private static final Collection<String> EXTENSIONS;
	static {
		Collection<String> extensions = new HashSet<String>();
		extensions.add("HTML");
		extensions.add("HTML");
		extensions.add("TXT");
		EXTENSIONS = Collections.unmodifiableCollection(extensions);
	}
	
	//long time = System.nanoTime();
	
	private int my_capacity;
	
	private static final int DEFAULT_TIMEOUT = 2500;

	private  final BlockingQueue<Runnable> my_queue;

	private ThreadPoolExecutor my_tpe;

	private final Collection<String> my_visited = new HashSet<String>();

	public PageToRetrieve(final int the_max_thread_count, int the_capacity, URL[] ignore) {
		for (URL url : ignore) {
			my_visited.add(normalURL(url));
		}
		my_queue = new LinkedBlockingQueue<Runnable>();
		my_capacity = the_capacity;
		my_tpe = new ThreadPoolExecutor(0,
				the_max_thread_count, THREAD_LIFE, TimeUnit.MILLISECONDS, my_queue);
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
		if (my_queue.size() < my_capacity && !my_visited.contains(normal) && isValidURL(the_url)) {
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
//			synchronized (System.out){
//				System.out.println((System.nanoTime() - time) / 1000000 + " between starting previous thread");
//				time = System.nanoTime();
//			}
//			long start = System.nanoTime();
			my_page.time = System.nanoTime();
			StringBuilder sb = new StringBuilder();
			Scanner scanner;
			try {
				my_page.setMarkup(Jsoup.connect(my_page.getURL().toString()).timeout(DEFAULT_TIMEOUT).get());
				sendBack(my_page);
			} catch (Exception e) {
				// TODO nothing
//				System.out.println((System.nanoTime() - start) / 1000000 + " time wasted");

			}	
			
		}

	}
	
	protected boolean hasEnqueued(final URL the_url) {
		return my_visited.contains(normalURL(the_url));
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
