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
public class Main {

	/**
	 * All the command line options.
	 * 
	 * @author joncaddey
	 * @version 1.0
	 */
	public enum Option {
		HELP(new String[] { "-h", "--help", "-?" }, true),
		LIMIT(new String[] { "-l","--limit" }, false),
		KEYWORDS(new String[]{"-k", "--keywords"}, false),
		RETRIEVERS(new String[] { "-r", "--retrievers" }, false),
		PARSERS(new String[] { "-p", "--parsers" }, false),
		SEED(new String[] { "-s", "--seed", "-u", "--url" }, false),
		TRIALS(new String[] { "-t", "--trials" }, false),
		IGNORE(new String[] {"-i", "--ignore"}, false);

		private final String[] my_options;
		private final boolean my_freestanding;

		private Option(final String[] the_options, final boolean the_freestanding) {
			my_options = the_options;
			my_freestanding = the_freestanding;
		}

		/**
		 * Given the command line option, returns the Option it represents. The
		 * dash or double dash should be included. Comparison is
		 * case-insensitive.
		 * 
		 * @param the_string representing an option.
		 * @return the Option, or null if there is no Option corresponding to the_string.
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
		
		/**
		 * @return whether this option expects an argument.
		 */
		public boolean isFreeStanding() {
			return my_freestanding;
		}
	}
	
	private static final String WHITESPACE_DELIMITER = "\\s+";
	private static final String ERR_UNRECOGNIZED_OPTION = "\'%s\' is not recognized as an option.  See readme for details.\n";
	private static final int DEFAULT_LIMIT = 10;
	private static final int MAX_LIMIT = 10000;
	private static final int MAX_KEYWORDS = 10;
	private static final List<String> DEFAULT_KEYWORDS = Arrays.asList(new String[] {
		"intelligence", "artificial", "agent", "university", "research", "science", "robot"
	});
	private static final int DEFAULT_PARSERS = 1;
	private static final int DEFAULT_RETRIEVERS = 1;
	private static final String DEFAULT_SEED = "http://faculty.washington.edu/gmobus/";
	private static final String DEFAULT_IGNORE = "http://questioneverything.typepad.com/";
	private static final int DEFAULT_TRIALS = 1;
	
	
	private static int my_limit = DEFAULT_LIMIT;
	private static List<String> my_keywords = new ArrayList<String>(DEFAULT_KEYWORDS);
	private static int my_parsers = DEFAULT_PARSERS;
	private static int my_retrievers = DEFAULT_RETRIEVERS;
	private static URL my_seed;
	private static URL[] my_ignore;
	private static int my_trials = DEFAULT_TRIALS;
	private static boolean my_go;

	public static void main(final String[] the_args) {
	
		// Initialize URLs
		try {
			my_ignore = new URL[]{new URL(DEFAULT_IGNORE)};
			my_seed = new URL(DEFAULT_SEED);
		} catch (MalformedURLException e) {
		}
		
		// Waiting is null whenever the previous argument was not an option.
		Option waiting = null;
		my_go = true;
		
		for (int pos = 0; pos < the_args.length; pos++) {
			String arg = the_args[pos];
			
			
			if (waiting == null) {
				// expecting an option
				// verbose option
				if (arg.startsWith("--")) {
					waiting = Option.getOption(arg);
					if (waiting == null) {
						System.out.printf(ERR_UNRECOGNIZED_OPTION, arg);
						my_go = false;
					} else if (waiting.isFreeStanding()) {
						handleArgument(waiting, null);
						waiting = null;
					}
				} else if (arg.startsWith("-")) {
					if (arg.length() == 2) {
						waiting = Option.getOption(arg);
						if (waiting == null) {
							System.out.printf(ERR_UNRECOGNIZED_OPTION, arg);
							my_go = false;
						} else if (waiting.isFreeStanding()) {
							handleArgument(waiting, null);
							waiting = null;
						}
					} else if (arg.length() > 2) {
						// waiting should stay null, since argument is next to option
						String opstr = arg.substring(0, 2);
						Option option = Option.getOption(opstr);
						if (option == null) {
							System.out.printf(ERR_UNRECOGNIZED_OPTION, arg);
							my_go = false;
						} else {
							handleArgument(option, arg.substring(2, arg.length()));
							waiting = null;
						}
					} else {
						// single '-' do nothing.
					}
				} else {
					System.err.println("Expected an option but found \'" + arg + "\' instead");
				}
			// expecting an argument
			} else {
				handleArgument(waiting, the_args[pos]);
				waiting = null;
			}
		}
		if (my_go) {
			start();
		}
	}
	
	/**
	 * Interprets an argument as if preceded by a given option.
	 * 
	 * @param the_current option for the argument.
	 * @param the_argument represented by a string containing the argument.
	 * @throws NumberFormatException
	 *             if something besides an integer is encountered when expected.
	 */
	// TODO this is not robust.
	private static void handleArgument(final Option the_current,
			final String the_argument) throws NumberFormatException {
		switch (the_current) {
		case LIMIT:
			try {
				my_limit = Integer.parseInt(the_argument);
				if (my_limit > MAX_LIMIT) {
					my_limit = MAX_LIMIT;
				} else if (my_limit < 1) {
					my_limit = 1;
				}
			} catch (NumberFormatException the_e) {
				System.out.println("\'" + the_argument
						+ "\'  must be an integer number of pages.");
				my_go = false;
			}
			break;
		case KEYWORDS: {
				my_keywords = new ArrayList<String>();
				Scanner scanner = new Scanner(the_argument);
				scanner.useDelimiter(WHITESPACE_DELIMITER);
				while (scanner.hasNext() && my_keywords.size() < MAX_KEYWORDS) {
					my_keywords.add(scanner.next().toLowerCase());
				}
			}
			break;
		case RETRIEVERS:
			try {
				my_retrievers = Integer.parseInt(the_argument);
			} catch (NumberFormatException the_e) {
				System.out.println("\'" + the_argument + "\' must be an integer number of parsers.");
				my_go = false;
			}
			if (my_retrievers < 1) {
				my_retrievers = 1;
			}
			
			break;
		case PARSERS:
			try {
				my_parsers = Integer.parseInt(the_argument);
			} catch (NumberFormatException the_e) {
				System.out.println("\'" + the_argument +"\' must be an integer number of retrievers.");
				my_go = false;
			}
			if (my_parsers < 1) {
				my_parsers = 1;
			}
			break;
		case SEED:
			try {
				my_seed = new URL(the_argument);
			} catch (MalformedURLException e1) {
				System.out.println(the_argument + " could not be resolved as a URL.");
				my_go = false;
			}
			break;
		case TRIALS:
			try {
				my_trials = Integer.parseInt(the_argument);
			} catch (NumberFormatException the_e) {
				System.out.println("\'" + the_argument +"\' must be an integer number of trials.");
				my_go = false;
			}
			if (my_trials < 1) {
				my_trials = 1;
			}
			break;
		case IGNORE: {
				List<URL> ignore = new ArrayList<URL>();
				Scanner scanner = new Scanner(the_argument);
				scanner.useDelimiter(WHITESPACE_DELIMITER);
				while (scanner.hasNext()) {
					String line = scanner.next();
					try {
						ignore.add(new URL(line));
					} catch (MalformedURLException e) {
						System.err.println(line + " could not be resolved as a URL.");
						my_go = false;
					}
				}
				my_ignore = new URL[ignore.size()];
				for (int i = 0; i < my_ignore.length; i++) {
					my_ignore[i] = ignore.get(i);
				}
			}
			break;
		case HELP:
			System.out.println("See readme for details.");
			my_go = false;
		default:
		break;
		
		}
	}
	
	private static void start() {
		ConsoleReporter reporter = new ConsoleReporter();
		DataGatherer gatherer = new DataGatherer(my_keywords, my_limit, my_trials, reporter);
		Controller controller = new Controller(my_seed, my_ignore, my_retrievers, my_parsers, gatherer);
		controller.start();
	}
}
