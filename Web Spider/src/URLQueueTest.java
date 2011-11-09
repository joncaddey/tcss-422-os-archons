/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * 2011-10-30
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author joncaddey
 * @version 1.0
 */
public class URLQueueTest {
	private static URL VALID_HTML;
	private static URL VALID_TXT;
	private static URL VALID_HOST;
	private static URL NOT_VALID;
	private static URL NOT_VALID_PHP;
	private static URL NO_ANCHOR;
	private static URL ANCHOR;
	
	private PageToRetrieve my_queue;
	
	@BeforeClass
	public static void setupClass() {
		try {
			VALID_HTML = new URL("http://faculty.washington.edu/gmobus/Academics/TCSS422/Projects/program1.html");
			VALID_TXT = new URL("http://kotaku.com/robots.txt");
			VALID_HOST = new URL("http://kotaku.com");
			NOT_VALID = new URL("mailto://gmobus@u.washington.edu");
			NOT_VALID_PHP = new URL("http://php.net/manual/en/ref.strings.php");
			NO_ANCHOR = new URL("http://en.wikipedia.org/wiki/Sudoku_algorithms");
			ANCHOR = new URL("http://en.wikipedia.org/wiki/Sudoku_algorithms#Solving_Sudokus_by_a_brute-force_algorithm");
		} catch (MalformedURLException the_e) {
			System.err.println(the_e);
		}
	}
	
	@Before
	public void setup() {
		my_queue = new PageToRetrieve(1, new URL[]{});
	}
	
	@Test
	public void isValidURLValidTest() {
		assertTrue(my_queue.isValidURL(VALID_HTML));
		assertTrue(my_queue.isValidURL(VALID_TXT));
		assertTrue(my_queue.isValidURL(VALID_HOST));
	}
	
	@Test
	public void isValidURLInvalidTest() {
		assertFalse(my_queue.isValidURL(NOT_VALID));
		assertFalse(my_queue.isValidURL(NOT_VALID_PHP));
	}
	
	@Test
	public void normalizeURLTest() {
		assertEquals(PageToRetrieve.normalURL(NO_ANCHOR), PageToRetrieve.normalURL(ANCHOR));
	}
	
	@Test
	public void constructorIgnoreTest() {
		PageToRetrieve queue = new PageToRetrieve(1, new URL[]{VALID_HTML});
		assertTrue(queue.hasEnqueued(VALID_HTML));
		assertFalse(queue.hasEnqueued(VALID_TXT));	
	}

}
