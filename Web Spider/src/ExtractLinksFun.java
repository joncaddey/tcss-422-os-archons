import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * 2011-10-22
 */

public class ExtractLinksFun {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String html = "<p>An <a href='http://example.com/'><b>example</b></a> link.</p>";
		Document doc = null;
		try {
			doc = Jsoup
					.connect(
							"http://kotaku.com")
					.get();
		} catch (final IOException the_e) {
			System.err.println(the_e);
		}

		System.out.println("Starting");


		String text = doc.body().text();

		Elements links = doc.body().getElementsByTag("a");
		for (Element link : links) {
			if (link.attr("href").endsWith("html")) {
				System.out.println("Link:");
				System.out.println(link.attr("href"));
				
			}
		}

	}

}
