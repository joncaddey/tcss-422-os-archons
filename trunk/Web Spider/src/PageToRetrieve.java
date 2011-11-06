import java.net.MalformedURLException;
import java.net.URL;
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
 * Nov 3, 2011
 */

/**
 * @author Travis Jensen
 * @version 1.0
 */
public class PageToRetrieve 
{
	private ThreadPoolExecutor my_page_retriever;
	
	private PageBuffer my_page_buffer;
	
	private int my_pages_visited;
	
	private final int my_max_pages_visited;
	
	public PageToRetrieve(final ThreadPoolExecutor the_page_retriever, final PageBuffer the_page_buffer,
						  final int the_max_pages_visited)
	{
		my_page_retriever = the_page_retriever;
		my_page_buffer = the_page_buffer;
		my_max_pages_visited = the_max_pages_visited;
	}
	
	public void executeTask(final URL the_url)
	{
		my_pages_visited++;
		if (my_pages_visited <= my_max_pages_visited)
		{
			Page page = new Page(the_url);
			RunPageRetriever pr = new RunPageRetriever(page, my_page_buffer);
			my_page_retriever.execute(pr);
		}
	}
}
