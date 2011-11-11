/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * Nov 8, 2011
 */

import java.net.URL;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;


/**
 * Coordinates web page retrievers and parsers to gather information from the
 * Internet and service a DataGatherer. A Controller must have its start()
 * method invoked before it actually begins the crawl.
 * 
 * @author Travis Jensen
 * @author Jonathan Caddey
 * @version 1.0
 */
public class Controller implements Observer {
	
	/**
	 * How long a thread in a thread pool executor will wait without getting a task before dying.
	 */
	public static final int THREAD_LIFE = 5000;
	
	/**
	 * The time in between trials for the main thread to wait.  This is time to let old threads finish
	 * on their own.  As such, it should be larger than the timeout used for retrieving pages.
	 */
	private static final int BREAK_TIME = 3000;
	
	/**
	 * The maximum capacity of the pageToRetrieve buffer.
	 */
	private static final int MAX_CAPACITY = 1000;
	
	private PageToRetrieve my_page_to_retrieve;
	private PageBuffer my_page_buffer;
	
	/**
	 * The DataGatherer who I service.
	 */
	private final DataGatherer my_data_gatherer;
	
	/**
	 * The URL to start the crawl at.
	 */
	private final URL my_seed;
	
	/**
	 * URLs to ignore during the crawl.
	 */
	private final URL[] my_ignored;
	
	/**
	 * The number of page retrieving threads to use.
	 */
	private final int my_num_retrievers;
	
	/**
	 * The number of page parsing threads to use.
	 */
	private final int my_num_parsers;

	/**
	 * Whether I am running.  I am temporarily stopped in between trials.
	 */
	private boolean my_running;
	
	
	/**
	 * Constructs a Controller.
	 * @param the_seed the URL to start at.
	 * @param the_ignored URLs to not visit.
	 * @param the_num_retrievers the number of page retrieving threads to use at one time.
	 * @param the_num_parsers the number of page parsing threads to use at one time.
	 * @param the_data_gatherer a data gatherer to service.
	 */
	public Controller(final URL the_seed, final URL[] the_ignored, final int the_num_retrievers,
			final int the_num_parsers, final DataGatherer the_data_gatherer) {
		my_data_gatherer = the_data_gatherer;
		my_data_gatherer.addObserver(this);
		my_seed = the_seed;
		my_ignored = the_ignored.clone();
		my_num_retrievers = the_num_retrievers;
		my_num_parsers = the_num_parsers;
	}
	
	/**
	 * Tells this to start crawling.  If it is already running, the invocation is ignored.
	 */
	public void start() {
		if (!my_running && my_data_gatherer.needsMoreData()) {
			my_page_to_retrieve = new PageToRetrieve(my_num_retrievers, Math.min(my_data_gatherer.getPageLimit(), MAX_CAPACITY), my_ignored);
			my_page_to_retrieve.addObserver(this);
			my_page_buffer =  new PageBuffer(my_num_parsers, my_data_gatherer);
			my_page_buffer.addObserver(this);
			my_running = true;
			my_data_gatherer.startTrial();
			my_page_to_retrieve.enqueue(my_seed);
		}
	}
	
	/**
	 * Shuts down any ExecutorServices and clears their queues.
	 */
	private void cleanUp() {
		my_page_to_retrieve.deleteObserver(this);
		my_page_to_retrieve.shutdown();
		my_page_to_retrieve = null;
		my_page_buffer.deleteObserver(this);
		my_page_buffer.shutdown();
		my_page_buffer = null;
	}

	@Override
	public void update(Observable the_observable, Object the_msg) {
		if (my_running) {
			if (the_observable == my_data_gatherer && the_msg instanceof Boolean) {
				// Our data gatherer has enough information for the trial.
				my_running = false;
				cleanUp();
				if (my_data_gatherer.needsMoreData()) {
					try {
						Thread.sleep(BREAK_TIME);
					} catch (InterruptedException e) {
					}
					start();
				}
			} else if (the_observable == my_page_to_retrieve && the_msg instanceof Page) {
				// Our PageToRetrieve has just retrieved a page and is giving it to us.
				my_page_buffer.enqueue((Page) the_msg);

			} else if (the_observable == my_page_buffer && the_msg instanceof Collection<?>) {
				// Our PageBuffer has just given us a bunch of URLS to enqueue
				for (URL url : (Collection<URL>) the_msg) {
					my_page_to_retrieve.enqueue(url);
				}
			}


		}
	}

	
}
