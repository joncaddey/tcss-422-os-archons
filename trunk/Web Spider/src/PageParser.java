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
public class PageParser implements Runnable
{
	private Page my_page;
	
	private DataGatherer my_dg;
	
	public PageParser(final Page the_page, final DataGatherer the_dg)
	{
		my_page = the_page;
		my_dg = the_dg;
	}
	
	public void run()
	{
		//Look at the_page's markup and give the DataGatherer info and PageToRetrieve more URL's.
	}
}
