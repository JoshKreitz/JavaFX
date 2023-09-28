package movieManager.movieShelf;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.FlowPane;
import movieManager.ConfigFile;
import movieManager.metadata.MetadataManager;
import movieManager.metadata.MovieMetadata;
import movieManager.metadata.NetworkHandler;

public class ShelfController implements Initializable {

	@FXML private FlowPane flowPane;

	@FXML private Label loadingLabel;
	@FXML private ProgressIndicator loadingSpinner;

	private ConfigFile config;
	private MetadataManager metadataManager;
	private NetworkHandler networkHandler;

	private List<MoviePane> moviePanes;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	public void initData(ConfigFile config, MetadataManager metadataManager, NetworkHandler networkHandler) {
		if (this.config != null) {
			throw new IllegalStateException("ConfigFile can only be initialized once");
		}

		this.config = config;
		this.metadataManager = metadataManager;
		this.networkHandler = networkHandler;

		// bind the loading stuff so the network handler can control the elements
		loadingLabel.textProperty().bind(networkHandler.getLoadingMessageProperty());
		loadingLabel.visibleProperty().bind(networkHandler.getDisplayLoadingSpinnerProperty());
		loadingSpinner.visibleProperty().bind(networkHandler.getDisplayLoadingSpinnerProperty());

		loadMovies(metadataManager.getAllMetadata());
	}

	private void loadMovies(Map<String, MovieMetadata> metadata) {
		moviePanes = new ArrayList<MoviePane>();

		// create all the moviepanes
		for (String key : metadata.keySet()) {
			MovieMetadata meta = metadata.get(key);
			moviePanes.add(new MoviePane(meta));
		}

		// sort em using compareTo in MoviePane
		Collections.sort(moviePanes);

		// add em to the flowpane
		ObservableList<Node> children = flowPane.getChildren();
		for (MoviePane p : moviePanes) {
			children.add(p);
		}
	}

}
