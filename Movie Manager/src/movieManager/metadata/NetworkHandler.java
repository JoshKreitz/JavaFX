package movieManager.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;

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
	private final String DB_URL = "https://api.themoviedb.org/3/search/movie";
	// an api_key generously donated by some dumb mf online
	private final String API_KEY = "15d2ea6d0dc1d476efbca3eba2b9bbfb";

	// the current movie metadata map
	Map<String, MovieMetadata> metadata;

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
	public void downloadMovies(Map<String, MovieMetadata> metadata, Set<String> filenames) {
		this.metadata = metadata;

		filenames.stream().map(Movie::new).forEach(this::downloadMovie);

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
	private void downloadMovie(Movie movie) {
		// form the search query URL
		String url = String.format("%s?api_key=%s&query=%s", DB_URL, API_KEY,
				URLEncoder.encode(movie.title, StandardCharsets.UTF_8));

		// asynchronously send out the request
		CompletableFuture.runAsync(() -> {
			try {
				System.out.println("Making call for " + movie.title);
				makeAPICall(url);

			} catch (IOException e) {
				System.out.println("Error making API call to " + url + ": " + e.getMessage());
			}
		});
	}

	/**
	 * Make a single API call to the desired URL
	 * 
	 * @param urlString The target URL
	 * @throws IOException if the connection fucks off for whatever reason
	 */
	private static void makeAPICall(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		try {
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");

			int responseCode = connection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) { // Success

				InputStream is = connection.getInputStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line = null; (line = reader.readLine()) != null;) {
					// builder.append(line).append("\n");
					System.out.println(line);

//					System.out.println("before");
//					System.out.println("after");
//					Map<String, String> map = mapper.readValue(json, Map.class);
//					
//					// or like this:
//					//Map<String, String> map = mapper.readValue(json, new TypeReference<Map<String, String>>(){});
//
//					map.forEach((k, v) -> System.out.format("[key]:%s \t[value]:%s\n", k, v));

					try {
						System.out.println("trying to parse");
						SearchResults results = mapper.readValue(line, SearchResults.class);

						// compact print
						System.out.println("\t" + urlString + " => NUMBER OF RESULTS: " + results.getResults().size());

						// pretty print
						// String prettyStaff1 =
						// mapper.writerWithDefaultPrettyPrinter().writeValueAsString(results);
						// System.out.println("\t" + prettyStaff1);

					} catch (IOException e) {
						System.out.println("shit failed");
						e.printStackTrace();
					}

				}

				is.close();
			} else {
				System.out.println("Failed API call for URL: " + urlString + ". Response code: " + responseCode);
			}
		} finally {
			connection.disconnect();
		}
	}

	/**
	 * A simple object to parse a filename and keep the extracted title associated
	 * with it's release date year. This assumes the filename follows the syntax "V
	 * for Vendetta (2006) 720p" where either or both of the year/resolution may be
	 * absent.
	 */
	private class Movie {
		public String title;
		public String year;

		// parse out the title and year
		public Movie(String filename) {
			// remove trailing "720p" or "1080p"
			filename.replaceAll("\\s*(\\d{3,4}p)$", "");

			// parse the date
			int parenIndex = filename.indexOf('(');
			int parenEndingIndex;
			if (parenIndex != -1 && (parenEndingIndex = filename.indexOf(')')) != -1
					&& parenEndingIndex - parenIndex == 5) {
				try {
					String tmp = filename.substring(parenIndex + 1, parenEndingIndex);
					Integer.parseInt(tmp);
					year = tmp;
				} catch (NumberFormatException e) {
				}

				title = filename.substring(0, parenIndex).trim();
			} else {
				year = "";
				title = filename;
			}
		}
	}

	public BooleanProperty getDisplayLoadingSpinnerProperty() {
		return displayLoadingSpinnerProperty;
	}

	public StringProperty getLoadingMessageProperty() {
		return loadingMessageProperty;
	}
}
