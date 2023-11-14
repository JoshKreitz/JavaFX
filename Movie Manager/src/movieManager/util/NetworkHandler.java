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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;
import movieManager.metadata.MetadataManager;
import movieManager.metadata.MovieFile;
import movieManager.metadata.MovieMetadata;

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
	private BooleanProperty displayLoadingSpinnerProperty = new SimpleBooleanProperty(true);
	private StringProperty loadingMessageProperty = new SimpleStringProperty("default");

	// a mapper used to parse the JSON responses from the API
	static final ObjectMapper mapper = new ObjectMapper();

	public NetworkHandler() {
	}

	/**
	 * Download metadata for the provided filenames and update it in the metadata
	 * map
	 * 
	 * @param metadata  The core metadata map to be updated
	 * @param filenames The files to be searched and downloaded
	 */
	@Deprecated
	// TODO remove
	public void downloadMovies(Map<String, MovieMetadata> metadata, Set<String> filenames) {

		// TODO add UI element controls
		loadingMessageProperty.set("TEST TEST");

		// TODO REMOVE
		PauseTransition delay = new PauseTransition(Duration.seconds(3));
		delay.setOnFinished(e -> {
			System.out.println("gg");
			loadingMessageProperty.set("get so fucked");
		});
		delay.playFromStart();
		PauseTransition delay2 = new PauseTransition(Duration.seconds(4));
		delay2.setOnFinished(e -> {
			System.out.println("cya");
			displayLoadingSpinnerProperty.set(false);
		});
		delay2.playFromStart();
	}

	/**
	 * Download the metadata for a single movie
	 * 
	 * @param movie
	 */
	public static void downloadMovie(MovieFile movie, MetadataManager manager) {
		// form the search query URL
		String url = String.format("%s?api_key=%s&query=%s", DB_URL, API_KEY,
				URLEncoder.encode(movie.getTitle(), StandardCharsets.UTF_8))
				+ (movie.hasYear() ? "&year=" + URLEncoder.encode(movie.getYear(), StandardCharsets.UTF_8) : "");

		// asynchronously send out the request
		CompletableFuture.runAsync(() -> {
			System.out.println("Making call for \"" + movie.getTitle() + "\" with year \"" + movie.getYear() + "\"");
			HttpURLConnection connection = openConnection(url);
			if (connection != null) {
				SearchResults results = parseSearchResults(url, connection);
				connection.disconnect();
				System.out.println("Got results, calling handler: " + results);
				manager.handleSearchResults(movie, results);
			}
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
				System.out.println("Failed API call for URL: " + urlString + ". Response code: " + responseCode);
				return null;
			}
		} catch (IOException e) {
			System.out.println("Error making API call to " + urlString + ": " + e.getMessage());
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

		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");

			return connection;
		} catch (MalformedURLException e) {
			System.out.println("Failed to create URL " + urlString + ": " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error making API call to " + urlString + ": " + e.getMessage());
		}
		return null;
	}

	public BooleanProperty getDisplayLoadingSpinnerProperty() {
		return displayLoadingSpinnerProperty;
	}

	public StringProperty getLoadingMessageProperty() {
		return loadingMessageProperty;
	}
}
