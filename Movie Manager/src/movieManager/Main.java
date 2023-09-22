package movieManager;

import java.io.IOException;
import java.util.List;

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
import movieManager.movieShelf.ShelfController;

public class Main extends Application {
	TabPane root;
	ConfigFile config;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		root = new TabPane();
		root.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		config = new ConfigFile("test");

		loadTabs();

		Scene scene = new Scene(root, 1000, 700);
		scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());

		primaryStage.setTitle("Movie Manager");
		primaryStage.setMinHeight(200);
		primaryStage.setMinWidth(625);
		primaryStage.setMaximized(false);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void loadTabs() throws IOException {
		List<Tab> tabs = root.getTabs();

		FXMLLoader shelfLoader = new FXMLLoader(getClass().getResource("movieShelf/Shelf.fxml"));
		Tab shelfTab = new Tab("Shelf", shelfLoader.load());
		ShelfController shelfController = shelfLoader.getController();
		shelfController.initData(config);
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
						System.out.println("File Manager Tab");
						System.out.println("fileManagerController=" + fileManagerController);
					} else if (newValue == shelfTab) {
						System.out.println("Movie Shelf Tab");
						System.out.println("movieShelfController=" + shelfController);
					} else if (newValue == configTab) {
						System.out.println("Config Tab");
						System.out.println("configController=" + configController);
					} else {
						System.out.println("SOMETHING GOOFED");
					}
				});
	}
}
