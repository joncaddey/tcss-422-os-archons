/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * 2011-10-22
 */

import java.net.URL;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;

/**
 * Holds information about this page.
 * 
 * @author joncaddey
 * @version 1.0
 */
public class Page {

	/**
	 * The URL this describes.
	 */
	private final URL my_url;
	
	private StringBuilder my_sb;
	

	public Page(final URL the_url) {
		my_url = the_url;
	}
	
	public URL getURL() {
		return my_url;
	}
	
	public String getMarkup() {
		return my_sb.toString();
	}
	
	public void setMarkup(final StringBuilder the_sb) {
		my_sb = the_sb;
	}
	
}
