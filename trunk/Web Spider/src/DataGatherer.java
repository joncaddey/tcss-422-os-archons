import java.util.Map;

/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * 2011-10-23
 */

/**
 * @author joncaddey
 * @version 1.0
 */
public class DataGatherer {
	
	private int my_total_links;
	private int my_total_words;
	private int my_total_pages;
	private Map<String, Integer> my_frequencies;
	private Reporter my_reporter;

	public DataGatherer(final String[] the_keywords, Reporter the_reporter)
	{
		my_reporter = the_reporter;
	}
}
