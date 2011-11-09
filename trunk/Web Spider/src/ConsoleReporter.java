/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * Nov 5, 2011
 */

/**
 * @author Travis Jensen
 * @author Jonathan Caddey
 * @version 1.0
 */
public class ConsoleReporter
{
	public void report(final Data the_data) {
		/*
		 *
		 
Parsed: www.tacoma.washington.edu/calendar/
Pages Retrieved: 12
Average words per page: 321
Average URLs per page: 11
Keyword               Ave. hits per page       Total hits
  albatross               0.001                     3
  carrots                 0.01                      5
  everywhere              1.23                      19
  etc..........

  intelligence            0.000                     0

Page limit: 5000
Average parse time per page .001msec
Total running time:       0.96 sec

		 */
		System.out.println("Parsed: " + the_data.getURL());
		
		
	}

}
