import java.net.URL;
import java.util.Collection;
import java.util.Map;

/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * Nov 8, 2011
 */

/**
 * @author Travis Jensen
 * @version 1.0
 */
public class Data {

	private final URL my_url;
	private final int my_words;
	private final Map<String, Integer> my_frequencies;
	private final Collection<URL> my_urls;
	
	public Data (URL the_url, int the_words, Map<String, Integer> the_frequencies, Collection<URL> the_urls) {
		my_url = the_url;
		my_words = the_words;
		my_frequencies = the_frequencies;
		my_urls = the_urls;
	}
	
	public URL getURL() {
		return my_url;
	}
	
	public int getWords() {
		return my_words;
	}
	
	public Map<String, Integer> getFrequencies() {
		return my_frequencies;
	}
	
	public Collection<URL> getURLs() {
		return my_urls;
	}
}
