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
import java.util.List;
import java.util.Scanner;


/**
 * @author Travis Jensen
 * @author Jonathan Caddey
 * @version 1.0
 */
public class UI {
	public static final int MAX_THREAD_COUNT = 10;
	public static final int MILLI_SECONDS_KEEP_ALIVE = 3000;

	public static void main(final String[] the_args) {
		// Initialize the DataGatherer. (uses reporter as a reference to send
		// data to.)
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter keywords, separated by spaces:");
		Scanner keyword_scanner = new Scanner(scan.nextLine());
		keyword_scanner.useDelimiter("\\s+");
		List<String> keywords = new ArrayList<String>();
		while (keyword_scanner.hasNext()) {
			keywords.add(keyword_scanner.next());
		}
		System.out.println(keywords);
		System.out.println("Maximum pages visited: ");
		int maxPagesVisited = scan.nextInt();
		scan.close();
		Reporter reporter = new Reporter();
		DataGatherer dg = new DataGatherer(keywords, maxPagesVisited, reporter);

		// Initialize the PageBuffer. (uses dg as a reference so PageParsers can
		// send data to it.)
		PageBuffer pb = new PageBuffer(dg, MAX_THREAD_COUNT);

		URL ignore = null;
		try {
			ignore = new URL("http://questioneverything.typepad.com/");
		} catch (MalformedURLException e1) {
		}
		
		PageToRetrieve ptr = new PageToRetrieve(MAX_THREAD_COUNT, new URL[]{ignore});


		Controller controller = new Controller(pb, ptr);

		// Send PageToRetrieve the first URL.
		URL url;
		try {
			url = new URL("http://faculty.washington.edu/gmobus/");
			controller.start(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
