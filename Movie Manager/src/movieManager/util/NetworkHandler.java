package movieManager.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import movieManager.metadata.MetadataManager;
import movieManager.metadata.MovieFile;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A consolidated handler for all outgoing requests. This class is responsible
 * for populating metadata from an external movie API, and goes through several
 * simple flows to retrieve the desired data
 */
public class NetworkHandler {

	/*
	 * Order of operations: - parse filename into it's component parts
	 * (filemanager?) - call the search endpoint - parse results, parse release
	 * date, decide which result fits - populate metadata - download image to dir -
	 * hopefully UI updates?
	 * 
	 * 
	 * It's now sending requests out and getting responses, but a few problems with
	 * the metadata: - filenames still contain file types - list contains duplicates
	 */

	// movie search URL for the movie DB API
	private static final String DB_URL = "https://api.themoviedb.org/3/search/movie";
	// an api_key generously donated by some dumb mf online
	private static final String API_KEY = "15d2ea6d0dc1d476efbca3eba2b9bbfb";

	// properties bound to network-status UI elements on the Shelf page
	private static BooleanProperty displayLoadingSpinnerProperty = new SimpleBooleanProperty(true);
	private static StringProperty loadingMessageProperty = new SimpleStringProperty("default");

	// a mapper used to parse the JSON responses from the API
	private static final ObjectMapper mapper = new ObjectMapper();

	// the number of requests that are still in flight
	private static AtomicInteger numOpenRequests = new AtomicInteger(0);

	private static Logger logger = Logger.getLogger(NetworkHandler.class.getName());

	public NetworkHandler() {
		logger.fine("Initializing NetworkHandler");

		// TODO add UI element controls
//		loadingMessageProperty.set("TEST TEST");

		// TODO REMOVE
//		PauseTransition delay = new PauseTransition(Duration.seconds(3));
//		delay.setOnFinished(e -> {
//			System.out.println("gg");
//			loadingMessageProperty.set("get so fucked");
//		});
//		delay.playFromStart();
//		PauseTransition delay2 = new PauseTransition(Duration.seconds(4));
//		delay2.setOnFinished(e -> {
//			System.out.println("cya");
//			displayLoadingSpinnerProperty.set(false);
//		});
//		delay2.playFromStart();
	}

	/**
	 * Download the metadata for a single movie
	 * 
	 * @param movie
	 */
	public static void downloadMovie(MovieFile movie, MetadataManager manager) {
		logger.info(String.format("Downloading metadata for movie %s", movie.getFilename()));

		// form the search query URL
		String url = String.format("%s?api_key=%s&query=%s", DB_URL, API_KEY,
				URLEncoder.encode(movie.getTitle(), StandardCharsets.UTF_8))
				+ (movie.hasYear() ? "&year=" + URLEncoder.encode(movie.getYear(), StandardCharsets.UTF_8) : "");

		// asynchronously send out the request
		CompletableFuture.runAsync(() -> {
			logger.fine(String.format("Making call for \"%s\" with year \"%d\"", movie.getTitle(), movie.getYear()));
			logger.fine(String.format("Search URL: %s", url));

			numOpenRequests.getAndIncrement();
			updateUIToggles();

			HttpURLConnection connection = openConnection(url);
			if (connection != null) {
				SearchResults results = parseSearchResults(url, connection);
				connection.disconnect();
				if (results != null) {
					logger.fine(String.format("Recieved results for movie \"%s\" with year \"%d\"", movie.getTitle(),
							movie.getYear()));
					logger.finer(String.format("Results: %s", results));
					manager.handleSearchResults(movie, results);
				} else {
					logger.warning(String.format("Failed to fetch results for movie \"%s\"", movie));
				}
			}

			numOpenRequests.getAndDecrement();
			updateUIToggles();
			// TODO do I need a failure callback? retry requests or anything? update UI?
		});
	}

	/**
	 * Parse the Search Results from the request JSON
	 * 
	 * @param urlString  the URL used to fetch the response
	 * @param connection the connection to the API
	 * @return a parsed SearchResults object, or null if any errors occurred
	 */
	private static SearchResults parseSearchResults(String urlString, HttpURLConnection connection) {
		try {
			int responseCode = connection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) { // Success
				try (InputStream is = connection.getInputStream()) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					SearchResults results = mapper.readValue(reader.readLine(), SearchResults.class);

					// compact print
					// TODO remove
					System.out.println("\t" + urlString.substring(urlString.indexOf("&")) + " => NUMBER OF RESULTS: "
							+ results.getResults().size());

					// pretty print
					// String prettyStaff1 =
					// mapper.writerWithDefaultPrettyPrinter().writeValueAsString(results);
					// System.out.println("\t" + prettyStaff1);

					return results;
				}
			} else {
				logger.warning(
						String.format("Failed API call for URL \"%s\" (response code %d)", urlString, responseCode));
				return null;
			}
		} catch (IOException e) {
			logger.warning(String.format("Error making API call to \"%s\" (%s)", urlString, e.getMessage()));
			e.printStackTrace(System.err);
			return null;
		}
	}

	/**
	 * Opens a connection to the desired URL, with some basic error handling
	 * 
	 * @param urlString The target URL
	 * @throws IOException if the connection fucks off for whatever reason
	 */
	private static HttpURLConnection openConnection(String urlString) {
		HttpURLConnection connection = null;
		logger.finer(String.format("Opening connection to \"%s\"", urlString));

		try {
			URL url = new URL(urlString);
			logger.finer(String.format("URL created for \"%s\"", urlString));
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");

			return connection;
		} catch (MalformedURLException e) {
			logger.warning(String.format("Failed to create URL \"%s\" (%s)", urlString, e.getMessage()));
			e.printStackTrace(System.err);
		} catch (IOException e) {
			logger.warning(String.format("Error making API call to \"%s\" (%s)", urlString, e.getMessage()));
			e.printStackTrace(System.err);
		}
		return null;
	}

	private static void updateUIToggles() {
		int numOpen = numOpenRequests.get();
		boolean spinnderDisplayed = displayLoadingSpinnerProperty.get();
		if (numOpen > 0) {
			if (!spinnderDisplayed) {
				displayLoadingSpinnerProperty.set(true);
			}
			loadingMessageProperty.set(numOpen + "");
		} else {
			if (spinnderDisplayed) {
				displayLoadingSpinnerProperty.set(false);
			}
			loadingMessageProperty.set("");
		}
	}

	public BooleanProperty getDisplayLoadingSpinnerProperty() {
		return displayLoadingSpinnerProperty;
	}

	public StringProperty getLoadingMessageProperty() {
		return loadingMessageProperty;
	}
}
