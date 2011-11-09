
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Travis Jensen
 * @author Jonathan Caddey
 * @version 1.0
 */
public class PageBuffer extends Observable
{
	private DataGatherer my_dg;
	
	private ThreadPoolExecutor my_tpe;
	
	private Set<String> my_keywords;
	
	private int my_pages_retrieved;
	
	private boolean running = true;
		
	public PageBuffer(final DataGatherer the_dg, final int the_max_thread_count)
	{
		my_dg = the_dg;
		my_keywords = the_dg.getKeywords();
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		my_tpe = new ThreadPoolExecutor(the_max_thread_count, the_max_thread_count,
				3000, TimeUnit.MILLISECONDS, queue);
	}
	
	//Input from PageToRetrieve is the_page that has markup.
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
	 * @param the_urls the number of words found on the parsed page.
	 */
	// TODO TESTING COMPLETION DATE: 00-00-2011
	// TODO FINALIZED AND APPROVED DATE: 00-00-2011
	private synchronized void sendBack(URL the_url, int the_words, Map<String, Integer> the_frequencies, Collection<URL> the_urls) {
		if (my_pages_retrieved < my_dg.getPageLimit()) {
			my_pages_retrieved++;
			my_dg.process(new Data(the_url, the_words, the_frequencies, the_urls));
			my_dg.reportData();
			System.out.println(the_url);
			setChanged();
			notifyObservers(the_urls);
		} else {
			running = false;
			my_tpe.shutdown();
			setChanged();
			notifyObservers(false);
		}
		
		
	}
	
	
	private class PageParser implements Runnable {
		
		private Page my_page;
		
		private PageParser(final Page the_page)
		{
			my_page = the_page;
		}
		public void run()
		{
			// initialize keywords as being at 0
			Map<String, Integer> frequencies = new HashMap<String, Integer>();
			for (String s : my_keywords) {
				frequencies.put(s,  0);
			}
			
			// build document from markup
			Document doc = Jsoup.parse(my_page.getMarkup().toString());	
			
			// extract keywords
			int total_words = 0;
			String token;
			if (doc.body() == null || doc.body().text() == null) {
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
			
			// extract links
			Collection<URL> urls = new ArrayList<URL>(); 
			Elements links = doc.body().getElementsByTag("a");
			for (Element link : links) {
				try {
					urls.add(new URL(my_page.getURL(), link.attr("href")));
				} catch (MalformedURLException the_e) {
					/// TODO nothing
				}
			}
			sendBack(my_page.getURL(), total_words, frequencies, urls);
		}
	}
}
