package movieManager.metadata;

import java.awt.Point;
import java.util.List;
import java.util.Map;

import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;

public class NetworkHandler {

	/*
	 * Order of operations: - parse filename into it's component parts
	 * (filemanager?) - call the search endpoint - parse results, parse release
	 * date, decide which result fits - populate metadata - download image to dir -
	 * hopefully UI updates?
	 */

	Map<String, MovieMetadata> metadata;

	private BooleanProperty displayLoadingSpinnerProperty = new SimpleBooleanProperty(true);
	private StringProperty loadingMessageProperty = new SimpleStringProperty("default");

	public NetworkHandler() {
	}

	public void downloadMovies(Map<String, MovieMetadata> metadata, List<String> filenames) {
		this.metadata = metadata;

		filenames.stream().map(Movie::new).forEach(this::downloadMovie);
		loadingMessageProperty.set("TEST TEST");


		//TODO REMOVE
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
