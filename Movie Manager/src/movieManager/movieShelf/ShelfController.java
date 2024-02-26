package movieManager.movieShelf;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.MapProperty;
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
import movieManager.config.ConfigFile;
import movieManager.metadata.MetadataManager;
import movieManager.metadata.MovieMetadata;
import movieManager.util.NetworkHandler;

/**
 * The controller for the Shelf, a graphic movie display that populates and
 * caches metadata
 */
public class ShelfController implements Initializable {
	/**
	 * UI ELEMENTS
	 */

	@FXML private FlowPane flowPane;

	@FXML private Label loadingLabel;
	@FXML private ProgressIndicator loadingSpinner;

	/**
	 * LOCAL VARIABLES
	 */

	private ConfigFile config;
	private MetadataManager metadataManager;
	private NetworkHandler networkHandler;

	private Map<String, MoviePane> moviePanes;
	
	private static Logger logger = Logger.getLogger(ShelfController.class.getName());

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		logger.fine("Initializing ShelfController");
	}

	/**
	 * Establish references to core application handlers and begin the flow to load
	 * metadata
	 * 
	 * @param config
	 * @param metadataManager
	 * @param networkHandler
	 */
	public void initData(ConfigFile config, MetadataManager metadataManager, NetworkHandler networkHandler) {
		logger.fine("Initializing ShelfController data");
		if (this.config != null) {
			logger.severe("ConfigFile can only be initialized once");
			throw new IllegalStateException("ConfigFile can only be initialized once");
		}

		this.config = config;
		this.metadataManager = metadataManager;
		this.networkHandler = networkHandler;

		// bind the loading-related UI elements so the network handler can control their
		// state
		loadingLabel.textProperty().bind(networkHandler.getLoadingMessageProperty());
		loadingLabel.visibleProperty().bind(networkHandler.getDisplayLoadingSpinnerProperty());
		loadingSpinner.visibleProperty().bind(networkHandler.getDisplayLoadingSpinnerProperty());

		initializeMovies();
	}

	/**
	 * Create MoviePane UI elements for all of the movies and display them in the
	 * primary flowPane
	 * 
	 * @param metadata A map between filenames and their metadata
	 */
	private void initializeMovies() {
		Map<String, MovieMetadata> metadata = metadataManager.getAllMetadata();
		
		logger.info("Creating inital MoviePane elements");
		logger.finer(String.format("Current metadata %s", metadata));
		
		// create all the movie panes
		moviePanes = new HashMap<String, MoviePane>();
		for (String key : metadata.keySet()) {
			MovieMetadata meta = metadata.get(key);
			moviePanes.put(key, new MoviePane(meta));
		}

		// Sort them using the MoviePane CompareTo function add em to the flow pane
		ObservableList<Node> children = flowPane.getChildren();
		moviePanes.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.forEach(pane -> children.add(pane.getValue()));
	}
}
