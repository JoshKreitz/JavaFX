package movieManager.movieShelf;

import java.awt.Point;
import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.util.Duration;
import movieManager.metadata.MovieMetadata;

/**
 * A custom UI element to display a single movie based on it's metadata,
 * including a dynamically populated sub-pane to be displayed when the mouse is
 * hovering over the movie.
 */
public class MoviePane extends Pane implements Comparable<MoviePane> {
	/**
	 * UI ELEMENTS
	 */

	@FXML private Pane moviePane;
	@FXML private ImageView imageView;

	@FXML private Popup interactivePopup;
	@FXML private HBox popupContent;

	@FXML private Button tmpButton; // TODO remove

	/**
	 * LOCAL VARIABLES
	 */

	// the metadata object for this MoviePane, which can be accessed via the FXML as well
	private MovieMetadata metadata;

	/**
	 * Create the element and explicitly define this class as it's controller and
	 * root.
	 * 
	 * @param metadata
	 */
	public MoviePane(MovieMetadata metadata) {
		this.metadata = metadata;

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MoviePane.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * Load the image to display, and configure the popup to appear on specific
	 * mouse events.
	 */
	@FXML
	public void initialize() {
		Image image = new Image(metadata.getImagePath());
		imageView.setImage(image);

		moviePane.setOnMouseEntered(event -> {
			PauseTransition delay = new PauseTransition(Duration.seconds(.75));
			delay.setOnFinished(e -> {
				if (moviePane.isHover() || popupContent.isHover()) {
					Point mouseLocation = java.awt.MouseInfo.getPointerInfo().getLocation();
					interactivePopup.show(moviePane, mouseLocation.x + 2, mouseLocation.y + 2);
				}
			});
			delay.playFromStart();
		});

		moviePane.setOnMouseExited(event -> {
			PauseTransition delay = new PauseTransition(Duration.seconds(.5));
			delay.setOnFinished(e -> {
				if (!moviePane.isHover() && !popupContent.isHover()
						&& !popupContent.localToScreen(popupContent.getBoundsInLocal()).contains(event.getScreenX(),
								event.getScreenY())) {
					interactivePopup.hide();
				}
			});
			delay.playFromStart();
		});

		popupContent.setOnMouseExited(event -> {
			PauseTransition delay = new PauseTransition(Duration.seconds(.5));
			delay.setOnFinished(e -> {
				if (!moviePane.isHover() && !popupContent.isHover()
						&& !popupContent.localToScreen(popupContent.getBoundsInLocal()).contains(event.getScreenX(),
								event.getScreenY())) {
					interactivePopup.hide();
				}
			});
			delay.playFromStart();
		});

	}

	public MovieMetadata getMetadata() {
		return metadata;
	}

	/**
	 * Compare MoviePanes, sorted by title first and release date second
	 */
	@Override
	public int compareTo(MoviePane o) {
		int titleCmp = metadata.getTitle().compareTo(o.getMetadata().getTitle());
		if (titleCmp == 0) {
			return metadata.getReleaseDate().compareTo(o.getMetadata().getReleaseDate());
		}
		return titleCmp;
	}
}
