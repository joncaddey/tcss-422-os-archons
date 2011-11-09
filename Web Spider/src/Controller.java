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
	private final PageToRetrieve my_page_to_retrieve;
	private final PageBuffer my_page_buffer;
	private boolean running = true;

	public Controller(PageBuffer the_page_buffer, PageToRetrieve the_page_to_retrieve)
	{
		my_page_buffer = the_page_buffer;
		my_page_buffer.addObserver(this);
		my_page_to_retrieve = the_page_to_retrieve;
		my_page_to_retrieve.addObserver(this);
	}
	
	@Override
	public void update(Observable the_observable, Object the_msg)
	{
		if (!running) {
			return;
		}
		// Our pagetoretrieve has just retrieved a page and is giving it to us
		if (the_observable == my_page_to_retrieve && the_msg instanceof Page) {
			my_page_buffer.enqueue((Page) the_msg);
		} else if (the_observable == my_page_buffer
				&& the_msg instanceof Collection<?>) {
			for (URL url : (Collection<URL>) the_msg) {
				my_page_to_retrieve.enqueue(url);
			}
		} else if (the_observable == my_page_buffer && the_msg instanceof Boolean) {
			running = (Boolean) the_msg;
			System.out.println("Shutting down *Beeeowp*");
			my_page_to_retrieve.terminate();
		}
	}
	
	public void start(URL the_start) {
		my_page_to_retrieve.enqueue(the_start);
	}

}
