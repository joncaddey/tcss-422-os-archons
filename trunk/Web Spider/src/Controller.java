import java.net.URL;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * Nov 8, 2011
 */

/**
 * @author Travis Jensen
 * @version 1.0
 */
public class Controller implements Observer {
	private PageToRetrieve my_page_to_retrieve;
	private PageBuffer my_page_buffer;
	private final DataGatherer my_data_gatherer;
	private boolean my_running;
	private final URL my_seed;
	private final URL[] my_ignored;
	private final int my_num_retrievers;
	private final int my_num_parsers;
	

	
	private long last_time;
	private int wasted;
	public Controller(final URL the_seed, final URL[] the_ignored, final int the_num_retrievers,
			final int the_num_parsers, final DataGatherer the_data_gatherer) {
		my_data_gatherer = the_data_gatherer;
		my_data_gatherer.addObserver(this);
		my_seed = the_seed;
		my_ignored = the_ignored.clone();
		my_num_retrievers = the_num_retrievers;
		my_num_parsers = the_num_parsers;
	}
	
	
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
				my_running = false;
				cleanUp();
				if ((Boolean) the_msg) {
					start();
				}
			} else if (the_observable == my_page_to_retrieve
					&& the_msg instanceof Page) {
				// Our pagetoretrieve has just retrieved a page and is giving it
				// to us
				my_page_buffer.enqueue((Page) the_msg);

			} else if (the_observable == my_page_buffer
			// our pagebuffer has just given us a bunch of URLS to enqueue
					&& the_msg instanceof Collection<?>) {
				for (URL url : (Collection<URL>) the_msg) {
					my_page_to_retrieve.enqueue(url);
				}
			}

//			} else if (the_observable == my_page_buffer
//					&& the_msg instanceof Boolean) {
//				// Our pageBuffer has just told us that pageLimit has been
//				// reached and to shut down.
//				running = false;
//				System.out.println("Shutting down *Beeeowp*");
//				my_page_to_retrieve.shutdown();
//			}
		}
	}

	
	public void start() {
		if (!my_running && my_data_gatherer.needsMoreData()) {
			my_page_to_retrieve = new PageToRetrieve(my_num_retrievers, my_data_gatherer.getPageLimit() * 2, my_ignored);
			my_page_to_retrieve.addObserver(this);
			my_page_buffer =  new PageBuffer(my_num_parsers, my_data_gatherer);
			my_page_buffer.addObserver(this);
			my_running = true;
			my_data_gatherer.startTrial();
			my_page_to_retrieve.enqueue(my_seed);
		}
	}

}
