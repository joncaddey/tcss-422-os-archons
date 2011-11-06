/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * 2011-10-30
 */
import java.io.IOException;
import java.util.Scanner;

/**
 * @author joncaddey
 * @version 1.0
 */
public class PageRetriever {
	// TODO surely this has some field it could use.  why not have a static method otherwise?
    // TODO Be wary of timeouts! O.O
	public void retrieve(Page the_page) throws IOException {
		StringBuilder sb = new StringBuilder();
		Scanner scanner = new Scanner(the_page.getURL().openStream());
		while (scanner.hasNextLine()) {
			sb.append(scanner.nextLine());
			sb.append('\n');
		}
		the_page.setMarkup(sb);
	}
}
