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

public class MoviePane extends Pane {

	@FXML private Pane moviePane;
	@FXML private ImageView imageView;

	@FXML private Popup interactivePopup;
	@FXML private HBox popupContent;

	@FXML private Button tmpButton; // TODO remove
	

	private final SimpleStringProperty movieId = new SimpleStringProperty();

	public MoviePane(@NamedArg("movieId") String tmp) {
		this.movieId.set(tmp);

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MoviePane.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@FXML
	public void initialize() {
		Image image = new Image("file:images/photo.JPG");
		imageView.setImage(image);

		tmpButton.textProperty().bind(movieId);

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
			if (!popupContent.localToScreen(popupContent.getBoundsInLocal()).contains(event.getScreenX(),
					event.getScreenY())) {
				interactivePopup.hide();
			}
		});

		popupContent.setOnMouseExited(event -> {
			if (!popupContent.localToScreen(popupContent.getBoundsInLocal()).contains(event.getScreenX(),
					event.getScreenY())) {
				interactivePopup.hide();
			}
		});

	}
}
