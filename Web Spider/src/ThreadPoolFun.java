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
public class ThreadPoolFun 
{
	private ThreadPoolExecutor my_tpe;
	
	public ThreadPoolFun(final ThreadPoolExecutor the_tpe)
	{
		my_tpe = the_tpe;
	}
	
	public void executeTask(final Runnable task)
	{
		my_tpe.execute(task);
	}
	
	public void executeTask2(final String the_url)
	{
		RunPageRetriever pr = new RunPageRetriever(the_url);
		my_tpe.execute(pr);
	}
	
	public static void main (final String[] the_args)
	{
		//Initialize the ThreadPoolExecutor.
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(10, 10, 1, TimeUnit.SECONDS, queue);
		ThreadPoolFun fun = new ThreadPoolFun(tpe);
		
		//Send the TPE a runnable task that is our (Run)PageRetriever.
		//This may be sent from another TPE that is managing the sb's and new URL's.
		//It will send our TPE a new URL and we will execute a thread that will turn it into a sb.
		RunPageRetriever pr = new RunPageRetriever("http://google.com");
		fun.executeTask(pr);
		
		//Runnable task seems like it needs to hold the URL inside of it, otherwise how do we give it the URL?
	}
}
