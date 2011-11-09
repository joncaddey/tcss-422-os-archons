/*
 * OS Archons
 * 
 * Jonathan Caddey
 * 
 * Travis Jensen
 * 
 * Nov 4, 2011
 */
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author Travis Jensen
 * @author Jonathan Caddey
 * @version 1.0
 */
public class UI {
	private static final String WHITESPACE_DELIMITER = "\\s+";
	public static final int MAX_THREAD_COUNT = 10;
	public static final int MILLI_SECONDS_KEEP_ALIVE = 3000;

	/**
	 * All the command line options.
	 * 
	 * @author joncaddey
	 * @version 1.0
	 */
	public enum Option {
		HELP(new String[] { "-h", "--help", "-?" }),
		LIMIT(new String[] { "-l","--limit" }),
		KEYWORDS(new String[]{"-k", "--keywords"}),
		RETRIEVERS(new String[] { "-r", "--retrievers" }),
		PARSERS(new String[] { "-p", "--parsers" }),
		SEED(new String[] { "-s", "--seed", "-u", "--url" }),
		TRIALS(new String[] { "-t", "--trials" }),
		IGNORE(new String[] {"-i", "--ignore"});

		private final String[] my_options;

		private Option(final String[] the_options) {
			my_options = the_options;
		}

		/**
		 * Given the command line option, returns the Option it represents. The
		 * dash or double dash should be included. Comparison is
		 * case-insensitive.
		 * 
		 * @param the_string
		 *            an option.
		 * @return the Option, or null if there is no Option corrosponding to the_string.
		 */
		public static Option getOption(final String the_string) {
			String str = the_string.toLowerCase();
			for (Option o : Option.values()) {
				for (String s : o.my_options) {
					if (s.equals(str)) {
						return o;
					}
				}
			}
			return null;
		}
	}
	
	private static final int DEFAULT_LIMIT = 100;
	private static final int MAX_LIMIT = 10000;
	private static final List<String> DEFAULT_KEYWORDS = Arrays.asList(new String[] {
		"intelligence", "artificial", "agent", "university", "research", "science", "robot"
	});
	private static final int DEFAULT_PARSERS = 1;
	private static final int DEFAULT_RETRIEVERS = 1;
	private static final String DEFAULT_SEED = "http://faculty.washington.edu/gmobus/";
	private static final String[] DEFAULT_IGNORE = new String[] {"http://questioneverything.typepad.com/"};
	private static final int DEFAULT_TRIALS = 1;
	
	
	private static int my_limit = DEFAULT_LIMIT;
	private static List<String> my_keywords = new ArrayList<String>(DEFAULT_KEYWORDS);
	private static int my_parsers = DEFAULT_PARSERS;
	private static int my_retrievers = DEFAULT_RETRIEVERS;
	private static String my_seed = DEFAULT_SEED;
	private static String[] my_ignore = DEFAULT_IGNORE;
	private static int my_trials = DEFAULT_TRIALS;

	public static void main(final String[] the_args) {
	
		// waiting is null whenever the previous argument was not an option.
		Option waiting = null;
		
		for (int pos = 0; pos < the_args.length; pos++) {
			String arg = the_args[pos];
			
			// expecting an option
			if (waiting == null) {
				// verbose option
				if (arg.startsWith("--")) {
					waiting = Option.getOption(arg);
					if (waiting == null) {
						handleArgument(Option.HELP, null);
					}
				} else if (arg.startsWith("-")) {
					if (arg.length() == 2) {
						waiting = Option.getOption(arg);
						if (waiting == null) {
							handleArgument(Option.HELP, null);
						}
					} else if (arg.length() > 2) {
						//waiting should stay null, since argument is next to option
						handleArgument(Option.getOption(arg.substring(0, 2)), arg.substring(2, arg.length()));
						waiting = null;
					} else {
						// single '-' do nothing.
					}
				} else {
					System.out.println("Expected an option but found \'" + arg + "\' instead");
				}
			// expecting an argument
			} else {
				handleArgument(waiting, the_args[pos]);
				waiting = null;
			}
		}
		
	}

	/**
	 * Interprets an argument as if preceded by a given option.
	 * 
	 * @param the_current
	 *            the option for the argument.
	 * @param the_argument
	 *            the string containing the argument.
	 * @throws NumberFormatException
	 *             if something besides an integer is encountered when expected.
	 */
	// TODO this is not robust.
	private static void handleArgument(final Option the_current,
			final String the_argument) throws NumberFormatException {
		switch (the_current) {
		case LIMIT:
			my_limit = Integer.parseInt(the_argument);
			if (my_limit > MAX_LIMIT) {
				my_limit = MAX_LIMIT;
			}
			break;
		case KEYWORDS: {
				my_keywords = new ArrayList<String>();
				Scanner scanner = new Scanner(the_argument);
				scanner.useDelimiter(WHITESPACE_DELIMITER);
				while (scanner.hasNext()) {
					my_keywords.add(scanner.next().toLowerCase());
				}
			}
			break;
		case RETRIEVERS:
			my_retrievers = Integer.parseInt(the_argument);
			break;
		case PARSERS:
			my_parsers = Integer.parseInt(the_argument);
			break;
		case SEED:
			my_seed = the_argument;
			break;
		case TRIALS:
			my_trials = Integer.parseInt(the_argument);
			break;
		case IGNORE: {
				List<String> ignore = new ArrayList<String>();
				Scanner scanner = new Scanner(the_argument);
				scanner.useDelimiter(WHITESPACE_DELIMITER);
				while (scanner.hasNext()) {
					ignore.add(scanner.next().toLowerCase());
				}
				my_ignore = new String[ignore.size()];
				for (int i = 0; i < my_ignore.length; i++) {
					my_ignore[i] = ignore.get(i);
				}
			}
			break;
		case HELP:
		default:
			System.out.println("See readme.txt for usage and details");
		break;
		
		}
	}

	public static void mainz(final String[] the_args) {
		// Initialize the DataGatherer. (uses reporter as a reference to send
		// data to.)
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter keywords, separated by spaces:");
		Scanner keyword_scanner = new Scanner(scan.nextLine());
		keyword_scanner.useDelimiter("\\s+");
		List<String> keywords = new ArrayList<String>();
		while (keyword_scanner.hasNext()) {
			keywords.add(keyword_scanner.next());
		}
		System.out.println(keywords);
		System.out.println("Maximum pages visited: ");
		int maxPagesVisited = scan.nextInt();
		scan.close();
		Reporter reporter = new Reporter();
		DataGatherer dg = new DataGatherer(keywords, maxPagesVisited, reporter);

		// Initialize the PageBuffer. (uses dg as a reference so PageParsers can
		// send data to it.)
		PageBuffer pb = new PageBuffer(dg, MAX_THREAD_COUNT);

		URL ignore = null;
		try {
			ignore = new URL("http://questioneverything.typepad.com/");
		} catch (MalformedURLException e1) {
		}

		PageToRetrieve ptr = new PageToRetrieve(MAX_THREAD_COUNT,
				new URL[] { ignore });

		Controller controller = new Controller(pb, ptr);

		// Send PageToRetrieve the first URL.
		URL url;
		try {
			url = new URL("http://faculty.washington.edu/gmobus/");
			controller.start(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
