/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * Nov 4, 2011
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Holds Pages with non-null markup until they are ready to be parsed.  Once parsed, the data is sent to a DataGatherer
 * and any URLS that were parsed are sent to Observers.
 * @author Travis Jensen
 * @author Jonathan Caddey
 * @version 1.0
 */
public class PageBuffer extends Observable
{
	private DataGatherer my_dg;
	
	private ThreadPoolExecutor my_tpe;
	
	private Set<String> my_keywords;
	
	private boolean running = true;
	
	private BlockingQueue<Runnable> my_queue;
		
	public PageBuffer(final int the_max_thread_count, final DataGatherer the_dg)
	{
		my_dg = the_dg;
		my_keywords = the_dg.getKeywords();
		my_queue = new LinkedBlockingQueue<Runnable>();
		my_tpe = new ThreadPoolExecutor(the_max_thread_count, the_max_thread_count,
				Controller.THREAD_LIFE, TimeUnit.MILLISECONDS, my_queue);
	}
	
	/**
	 * Enqueues the given Page to be parsed. The URLs found on the page will be
	 * sent automatically to Observers. If this has been shut down, the
	 * invocation is ignored.
	 * 
	 * @param the_page
	 *            must have non-null markup.
	 */
	public void enqueue(final Page the_page)
	{
		if (running) {
			PageParser pp = new PageParser(the_page);
			my_tpe.execute(pp);
		}
	}
	
	
	/**
	 * All my threads communicate with me through this synchronized method.  I in turn communicate to the outside world.
	 * @param the_words total words found on the parsed page.
	 * @param the_frequencies the keywords and how many keywords were found on that page.
	 * @param the_urls the number of links found on the parsed page.
	 */
	private synchronized void sendBack(URL the_url, int the_words, Map<String, Integer> the_frequencies, Collection<URL> the_urls) {
			my_dg.process(the_url, the_words, the_urls.size(), the_frequencies);
			setChanged();
			notifyObservers(the_urls);
	}
	
	/**
	 * Parses a web page.
	 * @author Travis Jensen
	 * @author Jonathan Caddey
	 * @version 1.0
	 */
	private class PageParser implements Runnable {
		
		private Page my_page;
		
		/**
		 * @param the_page markup should be non-null.
		 */
		private PageParser(final Page the_page)
		{
			my_page = the_page;
		}
		
		/**
		 * Parses the markup of my Page. If any problems occur, the Page is
		 * never parsed and is silently ignored.
		 */
		public void run()
		{
			// initialize keywords as being at 0
			Map<String, Integer> frequencies = new HashMap<String, Integer>();
			for (String s : my_keywords) {
				frequencies.put(s,  0);
			}
			
			Document doc = my_page.getMarkup();
			
			
			// Extract keywords from the document.
			int total_words = 0;
			String token;
			if (doc == null || doc.body() == null) {
				return;
			}
			Scanner scanner = new Scanner(doc.body().text());
			Pattern pattern = Pattern.compile("\\s+");
			scanner.useDelimiter(pattern);
			while (scanner.hasNext()) {
				total_words++;
				token = (scanner.next().toLowerCase());
				Integer frequency = frequencies.get(token);
				if (frequency != null) {
					frequencies.put(token, frequency + 1);
				}
			}
			
			// Extract links from the document.
			Collection<URL> urls = new ArrayList<URL>(); 
			Elements links = doc.body().getElementsByTag("a");
			for (Element link : links) {
				try {
					urls.add(new URL(my_page.getURL(), link.attr("href")));
				} catch (MalformedURLException the_e) {
				}
			}
			sendBack(my_page.getURL(), total_words, frequencies, urls);
		}
	}
	
	/**
	 * Any enqueued Pages are discarded.
	 */
	public void shutdown() {
		my_tpe.shutdown();
		my_queue.clear();
	}
}
