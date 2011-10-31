/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * 2011-10-30
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;

// TODO this must be synchronized.
// TODO this probably shouldn't keep track of pages count.  URLs might not be able to be opened.
/**
 * @author joncaddey
 * @version 1.0
 */
public class URLQueue {

	private static final int MAX_PAGE_LIMIT = 10000;
	private static final int DEFAULT_PAGE_LIMIT = 5000;

	private static final Collection<String> EXTENSIONS;
	static {
		Collection<String> extensions = new HashSet<String>();
		extensions.add("HTML");
		extensions.add("HTML");
		extensions.add("TXT");
		EXTENSIONS = Collections.unmodifiableCollection(extensions);
	}

	private final Deque<URL> my_queue = new LinkedList<URL>();
	private final Collection<String> my_visited = new HashSet<String>();

	/**
	 * Crawl responsibly--know your page limit.
	 */
	private final int my_page_limit;
	
	private int my_page_count;

	/**
	 * Creates a URLQueue that can return a given number of URLS from its
	 * dequeue over its lifetime.
	 * 
	 * @param the_page_limit
	 *            the max number of URLs that could be dequeued.
	 */
	// TODO TESTING COMPLETION DATE: 00-00-2011
	// TODO FINALIZED AND APPROVED DATE: 00-00-2011
	public URLQueue(final int the_page_limit) {
		this(the_page_limit, null);
	}

	/**
	 * Creates a URLQueue with a page limit of 5000.
	 */
	// TODO TESTING COMPLETION DATE: 00-00-2011
	// TODO FINALIZED AND APPROVED DATE: 00-00-2011
	public URLQueue() {
		this(DEFAULT_PAGE_LIMIT, null);
	}
	
	/**
	 * @param the_page_limit
	 * @param the_ignored URLs that should not be visited.
	 */
	// TESTING COMPLETION DATE: 10-30-2011
	// TODO FINALIZED AND APPROVED DATE: 00-00-2011
	public URLQueue(final int the_page_limit, Collection<URL> the_ignored) {
		if (the_page_limit < 0 || the_page_limit > MAX_PAGE_LIMIT) {
			throw new IllegalArgumentException(
					"the page limit must be between 0 and " + MAX_PAGE_LIMIT);
		}
		if (the_ignored != null) {
			for (URL url : the_ignored) {
				my_visited.add(normalURL(url));
			}
		}
		my_page_limit = the_page_limit;
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
		if (!my_visited.contains(normal) && isValidURL(the_url) && my_page_count < my_page_limit) {
			my_queue.addFirst(the_url);
			my_page_count++;
			my_visited.add(normal);
		}
	}
	
	/**
	 * @return a URL to visit.
	 */
	// TODO TESTING COMPLETION DATE: 00-00-2011
	// TODO FINALIZED AND APPROVED DATE: 00-00-2011
	public URL deueue() {
		return my_queue.removeLast();
	}
	

	/**
	 * Whether the given URL will be enqueued if it has not been visited.
	 * 
	 * @param the_url a URL to potentially enqueue.
	 * @return whether the given url has no path or ends with htm, html, or txt.
	 */
	// TESTING COMPLETION DATE: 10-30-2011
	// TODO FINALIZED AND APPROVED DATE: 00-00-2011
	public boolean isValidURL(final URL the_url) {
		boolean valid = true;
		final String path = the_url.getPath();
		if (path.length() != 0) {
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
	 * @param the_url a URL to potentially enqueue.
	 * @return whether the given URL has been added before (and thus will not be added again).
	 */
	// TESTING COMPLETION DATE: 10-30-2011
	// TODO FINALIZED AND APPROVED DATE: 00-00-2011
	public boolean hasEnqueued(final URL the_url) {
		return my_visited.contains(normalURL(the_url));
	}
	
	/**
	 * @param the_url a URL to normalize.
	 * @return a representation of the URL used for determining whether two URLs are equal.
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
	
	public static void main(String args[]) throws MalformedURLException {
		//URL url = new URL("http://www.google.com/search?client=ubuntu&channel=fs&q=hello+world&ie=utf-8&oe=utf-8");
		//normalURL(url);
	}

}
