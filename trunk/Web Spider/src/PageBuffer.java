import java.net.URL;
import java.util.concurrent.ThreadPoolExecutor;

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
public class PageBuffer
{
	private ThreadPoolExecutor my_tpe;
	private DataGatherer my_dg;
		
	public PageBuffer(final ThreadPoolExecutor the_tpe, final DataGatherer the_dg)
	{
		my_tpe = the_tpe;
		my_dg = the_dg;
	}
	
	//Input from PageToRetrieve is the_page that has markup.
	public void executeTask(final Page the_page)
	{
		PageParser pp = new PageParser(the_page, my_dg);
		my_tpe.execute(pp);
	}
}
