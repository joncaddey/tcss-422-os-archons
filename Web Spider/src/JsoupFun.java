import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * 2011-10-22
 */

/**
 * @author joncaddey
 * @version 1.0
 */
public class JsoupFun {

	/**
	 * 
	 * @param args
	 */
	// TODO TESTING COMPLETION DATE: 00-00-2011
	// TODO FINALIZED AND APPROVED DATE: 00-00-2011
	public static void main(String[] args) {
		Document doc = null;
		StringBuilder sb = new StringBuilder();
		URL url = null;
		try {
			url = new URL("http://en.wikipedia.org/wiki/Werewolf");
			URLConnection myURLConnection = url.openConnection();
		    myURLConnection.connect();
		    Scanner scanner = new Scanner(myURLConnection.getInputStream());
		    while (scanner.hasNextLine()) {
		    	String line = scanner.nextLine();
		    	sb.append('\n');
		    	//System.out.println(line);
		    	sb.append(line);
		    }
		    myURLConnection.getInputStream().close();

			
//			doc = Jsoup
//					.connect(
//							"http://en.wikipedia.org/wiki/Werewolf")
//					.get();
		} catch (final IOException the_e) {
			System.err.println(the_e);
		} catch (Exception e) {
			System.err.println(e);
		}


		doc = Jsoup.parse(sb.toString());

		String text = doc.body().text();
		System.out.println(text.length());
		
		
		Scanner scanner = new Scanner(text);
		Pattern pattern = Pattern.compile("\\s+");
		scanner.useDelimiter(pattern);
		String token;
		Map<String, Integer> frequencies = new HashMap<String, Integer>();
		frequencies.put("thread", 0);
		frequencies.put("dynamics", 0);
		frequencies.put("C", 0);
		int total_words = 0;
		while (scanner.hasNext()) {
			total_words++;
			token = (scanner.next().toLowerCase());
			Integer frequency = frequencies.get(token);
			if (frequency != null) {
				frequencies.put(token, frequency + 1);
			}
		}
		Iterable<String> keys = frequencies.keySet();
		for (String key : keys) {
			System.out.println(key + ": " + frequencies.get(key));
		}
		

	}

}
