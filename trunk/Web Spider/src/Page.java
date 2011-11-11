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

import org.jsoup.nodes.Document;

/**
 * Holds information about this page.
 * 
 * @author joncaddey
 * @author Travis Jensen
 * @version 1.0
 */
public class Page {


	private final URL my_url;

	private Document my_document;

	public Page(final URL the_url) {
		my_url = the_url;
	}

	public URL getURL() {
		return my_url;
	}

	public Document getMarkup() {
		return my_document;
	}

	public void setMarkup(final Document the_document) {
		my_document = the_document;
	}
}
