/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * Nov 3, 2011
 */

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;

/**
 * Holds URLs until they are ready to be retrieved. Once retrieved, a Page
 * object representing the dequeued URL is sent to Observers.
 * 
 * @author Travis Jensen
 * @author Jonathan Caddey
 * @version 1.0
 */
public class PageToRetrieve extends Observable {

	/**
	 * How long a retriever will wait to retrieve a page before it gives up.
	 */
	private static final int DEFAULT_TIMEOUT = 2500;

	/**
	 * Acceptable extensions for web resources to visit.
	 */
	private static final Collection<String> EXTENSIONS;
	static {
		Collection<String> extensions = new HashSet<String>();
		extensions.add("HTML");
		extensions.add("HTML");
		extensions.add("TXT");
		EXTENSIONS = Collections.unmodifiableCollection(extensions);
	}

	private int my_capacity;

	private final BlockingQueue<Runnable> my_queue;

	private ThreadPoolExecutor my_tpe;

	/**
	 * Keeps track of what URLs are visited. The results of calls to normalURL
	 * should be used rather than simply the URLs toString.
	 */
	private final Collection<String> my_visited;

	/**
	 * Creates a queue to hold URLs before they are retrieved.
	 * 
	 * @param the_max_thread_count
	 *            the number of retrievers to run concurrently.
	 * @param the_capacity
	 *            the number of URLs to have queued at any given time.
	 * @param the_ignored
	 *            URLs that should not be visited.
	 */
	public PageToRetrieve(final int the_max_thread_count, int the_capacity,
			URL[] the_ignored) {
		// the_capacity is used for my_visited merely so it scales approximately
		// with the expected problem size.
		my_visited = new HashSet<String>(the_capacity);
		for (URL url : the_ignored) {
			my_visited.add(normalURL(url));
		}
		my_queue = new LinkedBlockingQueue<Runnable>();
		my_capacity = the_capacity;
		my_tpe = new ThreadPoolExecutor(the_max_thread_count,
				the_max_thread_count, Controller.THREAD_LIFE,
				TimeUnit.MILLISECONDS, my_queue);
	}

	/**
	 * Enqueue a URL to be retrieved automatically. Once retrieved, a Page
	 * object representing the URL is sent to Observers. URLs with paths must
	 * end with html, htm, or txt. Otherwise they are silently not enqueued.
	 * 
	 * @param the_url
	 *            to visit.
	 */
	public void enqueue(final URL the_url) {
		String normal = normalURL(the_url);
		if (my_queue.size() < my_capacity && !my_visited.contains(normal)
				&& isValidURL(the_url)) {
			my_visited.add(normal);
			my_tpe.execute(new PageRetriever(new Page(the_url)));
		}
	}

	/**
	 * Whether the given URL will be enqueued if it has not been visited.
	 * 
	 * @param the_url
	 *            to potentially enqueue.
	 * @return whether the given url has no path or ends with htm, html, or txt.
	 */
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

	/**
	 * All my threads communicate with me through this synchronized method. I in
	 * turn communicate to the outside world.
	 * 
	 * @param the_page
	 *            a Page with non-null markup.
	 */
	private synchronized void sendBack(final Page the_page) {
		setChanged();
		notifyObservers(the_page);
	}

	/**
	 * @param the_url
	 *            to normalize.
	 * @return a representation of the URL used for determining whether two URLs
	 *         are equal. Port and reference (anchor) is ignored.
	 */
	protected static String normalURL(final URL the_url) {
		StringBuilder sb = new StringBuilder();
		sb.append(the_url.getProtocol());
		sb.append("://");
		sb.append(the_url.getHost());
		sb.append(the_url.getPath());
		return sb.toString();
	}

	/**
	 * @param the_url
	 *            a url.
	 * @return whether the_url has been marked as visited (but not necessarily
	 *         retrieved).
	 */
	protected boolean hasEnqueued(final URL the_url) {
		return my_visited.contains(normalURL(the_url));
	}

	/**
	 * Enqueued URLs are discarded.
	 */
	public void shutdown() {
		my_tpe.shutdown();
		my_queue.clear();
	}

	/**
	 * Retrieves the markup of a page at a URL.
	 * 
	 * @author Travis Jensen
	 * @author Jonathan Caddey
	 * @version 1.0
	 */
	private class PageRetriever implements Runnable {

		private Page my_page;

		public PageRetriever(final Page the_page) {
			my_page = the_page;
		}

		/**
		 * Set the markup of a URL and read it onto a Page. If there is any
		 * problem retrieving the page, the page is silently discarded.
		 */
		public void run() {
			// not synchronized because it's just an accessor.
			if (my_tpe.isShutdown()) {
				return;
			}

			try {
				my_page.setMarkup(Jsoup.connect(my_page.getURL().toString())
						.timeout(DEFAULT_TIMEOUT).get());
				sendBack(my_page);
			} catch (Exception e) {
			}

		}

	}

}
