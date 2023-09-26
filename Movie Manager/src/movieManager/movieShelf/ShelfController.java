package movieManager.movieShelf;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import movieManager.ConfigFile;
import movieManager.metadata.MetadataManager;
import movieManager.metadata.MovieMetadata;

public class ShelfController implements Initializable {

	@FXML private FlowPane flowPane;

	private ConfigFile config;
	private MetadataManager metadataManager;

	private List<MoviePane> moviePanes;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	public void initData(ConfigFile config, MetadataManager metadataManager) {
		if (this.config != null) {
			throw new IllegalStateException("ConfigFile can only be initialized once");
		}

		this.config = config;
		this.metadataManager = metadataManager;
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
