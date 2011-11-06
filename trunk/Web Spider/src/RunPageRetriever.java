import java.io.IOException;
import java.util.Scanner;

/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * Nov 3, 2011
 */

/**
 * @author Travis Jensen
 * @version 1.0
 */
public class RunPageRetriever implements Runnable
{
	private Page my_page;
	
	private PageBuffer my_page_buffer;
	
	public RunPageRetriever(final Page the_page, final PageBuffer the_page_buffer)
	{
		my_page = the_page;
		my_page_buffer = the_page_buffer;
	}
	
	//Sets the markup from the URL in my_page.
	public void run()
	{
		StringBuilder sb = new StringBuilder();
		Scanner scanner;
		try {
			scanner = new Scanner(my_page.getURL().openStream());
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
				sb.append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		my_page.setMarkup(sb);
		
		//Send page to the PageBuffer with markup to decrypt.
		my_page_buffer.executeTask(my_page);
	}
}
