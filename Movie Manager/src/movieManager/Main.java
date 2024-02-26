package movieManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.stage.Stage;
import movieManager.config.ConfigController;
import movieManager.config.ConfigFile;
import movieManager.fileManager.FileManagerController;
import movieManager.metadata.MetadataManager;
import movieManager.movieShelf.ShelfController;
import movieManager.util.DelayResetLogManager;
import movieManager.util.NetworkHandler;

/*
 * This is the entry point of the application. This class establishes the root JavaFX Stage containing the top level tabs, and provides
 * those tabs with references to any configuration entities that must be shared between them. 
 */
public class Main extends Application {
	// The top level pane that holds the main page tabs
	TabPane root;

	// The core settings for the application, read from the application's config
	// file and supplied to the various tabs
	ConfigFile config;

	// The core movie metadata, read from a previous cache or downloaded from the
	// internet
	MetadataManager metadataManager;

	// The core network handler, which takes care of all external network calls
	NetworkHandler networkHandler;

	// Establish the custom logging manager to be used for all of the loggers
	static {
		System.setProperty("java.util.logging.manager", DelayResetLogManager.class.getName());
	}

	private static Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {
		initializeLogger();
		logger.info("Starting Movie Manager...");

		launch(args);
	}

	/**
	 * This callback is activated when the the JavaFX application is initialized and
	 * establishes the entire application
	 * 
	 * @param primaryStage the root JavaFX Stage
	 */
	@Override
	public void start(Stage primaryStage) throws IOException {
		root = new TabPane();
		root.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		config = new ConfigFile();
		this.networkHandler = new NetworkHandler();
		metadataManager = new MetadataManager(config);

		loadTabs();

		Scene scene = new Scene(root, 1000, 700);
		scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());

		primaryStage.setTitle("Movie Manager");
		primaryStage.setMinHeight(200);
		primaryStage.setMinWidth(625);
		primaryStage.setMaximized(false);

		logger.fine("Showing main scene");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Initialize the tab pages and provide them references to any shared entities.
	 * This function also adds a listener to detect tab changes.
	 * 
	 * @throws IOException if the tab classes fail to load
	 */
	private void loadTabs() throws IOException {
		List<Tab> tabs = root.getTabs();

		FXMLLoader shelfLoader = new FXMLLoader(getClass().getResource("movieShelf/Shelf.fxml"));
		Tab shelfTab = new Tab("Shelf", shelfLoader.load());
		ShelfController shelfController = shelfLoader.getController();
		shelfController.initData(config, metadataManager, networkHandler);
		tabs.add(shelfTab);

		FXMLLoader fileManagerLoader = new FXMLLoader(getClass().getResource("fileManager/FileManager.fxml"));
		Tab fileManagerTab = new Tab("File Manager", fileManagerLoader.load());
		FileManagerController fileManagerController = fileManagerLoader.getController();
		fileManagerController.initData(config);
		tabs.add(fileManagerTab);

		FXMLLoader configLoader = new FXMLLoader(getClass().getResource("config/Config.fxml"));
		Tab configTab = new Tab("Config", configLoader.load());
		ConfigController configController = configLoader.getController();
		configController.initData(config);
		tabs.add(configTab);

		root.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
					if (newValue == fileManagerTab) {
						fileManagerController.updateDefaultSourceDir();
//						System.out.println("File Manager Tab");
//						System.out.println("fileManagerController=" + fileManagerController);
					} else if (newValue == shelfTab) {
//						System.out.println("Movie Shelf Tab");
//						System.out.println("movieShelfController=" + shelfController);
					} else if (newValue == configTab) {
//						System.out.println("Config Tab");
//						System.out.println("configController=" + configController);
						// TODO save file before leaving?
					} else {
						System.out.println("SOMETHING GOOFED");
					}
				});
	}

	private static void initializeLogger() {
		logger.setLevel(Level.ALL);
		logger.info("Initializing Logger - attempting to load configuration file");

		LogManager logmanager = LogManager.getLogManager();

		try {
			InputStream configFile = Main.class.getResourceAsStream("/logging.properties");
			if (configFile == null) {
				throw new IOException();
			}
			LogManager.getLogManager().readConfiguration(configFile);
			logger.info("Successfully loaded logging configuration");
			logger.fine(String.format("Logging Level: %s", logmanager.getProperty(".level")));
			logger.fine(String.format("Logging Handler Level: %s",
					logmanager.getProperty("java.util.logging.ConsoleHandler.level")));
			logger.info(DelayResetLogManager.getLogManager().getClass().getName());
		} catch (IOException | NullPointerException ex) {
			System.out.println("WARNING: Could not open logging configuration file");
			System.out.println("WARNING: Logging not configured (console output only)");
		}
	}
}
