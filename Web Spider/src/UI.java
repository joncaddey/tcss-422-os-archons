import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * Nov 4, 2011
 */

/**
 * @author Travis Jensen
 * @version 1.0
 */
public class UI
{
	public static final int MAX_THREAD_COUNT = 10;
	public static final int MILLI_SECONDS_KEEP_ALIVE = 1000;
	
	public static void main(final String[] the_args)
	{
		//Initialize the DataGatherer. (uses reporter as a reference to send data to.)
		String[] keywords = new String[10];
		Scanner scan = new Scanner(System.in);
		System.out.println("How many key words?");
		int words = scan.nextInt();
		for (int i = 0; i < words; i++)
		{
			System.out.println("Keyword #" + i + ": ");
			keywords[i] = scan.nextLine();
		}
		
		Reporter reporter = new Reporter();
		DataGatherer dg = new DataGatherer(keywords, reporter);
		
		//Initialize the PageBuffer. (uses dg as a reference so PageParsers can send data to it.)
		BlockingQueue<Runnable> queue2 = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor tpe2 = new ThreadPoolExecutor(MAX_THREAD_COUNT, MAX_THREAD_COUNT,
				MILLI_SECONDS_KEEP_ALIVE, TimeUnit.MILLISECONDS, queue2);
		PageBuffer pb = new PageBuffer(tpe2, dg);
				
		//Initialize the PageToRetrieve. (uses pb as a reference so RunPageRetrievers can send data to it.)
		System.out.println("Maximum pages visited: ");
		int maxPagesVisited = scan.nextInt();
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(MAX_THREAD_COUNT, MAX_THREAD_COUNT,
				MILLI_SECONDS_KEEP_ALIVE, TimeUnit.MILLISECONDS, queue);
		PageToRetrieve ptr = new PageToRetrieve(tpe, pb, maxPagesVisited);
		
		//Send PageToRetrieve the first URL.
		URL url;
		try {
			url = new URL("http://faculty.washington.edu/gmobus/");
			ptr.executeTask(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}

