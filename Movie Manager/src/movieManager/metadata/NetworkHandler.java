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

public class NetworkHandler {

	/*
	 * Order of operations: - parse filename into it's component parts
	 * (filemanager?) - call the search endpoint - parse results, parse release
	 * date, decide which result fits - populate metadata - download image to dir -
	 * hopefully UI updates?
	 * 
	 * 
	 * It's now sending requests out and getting responses, but a few problems with the metadata:
	 * 		- filenames still contain file types
	 * 		- list contains duplicates
	 */

	Map<String, MovieMetadata> metadata;

	private BooleanProperty displayLoadingSpinnerProperty = new SimpleBooleanProperty(true);
	private StringProperty loadingMessageProperty = new SimpleStringProperty("default");
	
	private final String DB_URL = "https://api.themoviedb.org/3/search/movie";
	private final String API_KEY = "15d2ea6d0dc1d476efbca3eba2b9bbfb";

	static final ObjectMapper mapper = new ObjectMapper();

	public NetworkHandler() {
	}

	public void downloadMovies(Map<String, MovieMetadata> metadata, Set<String> filenames) {
		this.metadata = metadata;

		filenames.stream().map(Movie::new).forEach(this::downloadMovie);
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

	private void downloadMovie(Movie movie) {
		String url = String.format("%s?api_key=%s&query=%s", DB_URL, API_KEY, URLEncoder.encode(movie.title, StandardCharsets.UTF_8));
		
		CompletableFuture.runAsync(() -> {
			try {
				System.out.println("Making call for " + movie.title);
				makeAPICall(url);
			} catch (IOException e) {
				System.out.println("Error making API call to " + url + ": " + e.getMessage());
			}
		});
	}

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
				    //builder.append(line).append("\n");
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
			            //String prettyStaff1 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(results);
			            //System.out.println("\t" + prettyStaff1);


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
