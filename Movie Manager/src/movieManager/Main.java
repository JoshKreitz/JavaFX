package movieManager;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import movieManager.config.ConfigController;
import movieManager.config.ConfigFile;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		Parent fxmlLoader = FXMLLoader.load(getClass().getResource("Root.fxml"));

		primaryStage.setTitle("Movie Manager");

		primaryStage.setMinHeight(200);
		primaryStage.setMinWidth(625);
		primaryStage.setMaximized(false);


		Scene scene = new Scene(fxmlLoader, 1000, 700);
		scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.show();
		
		initData();
	}

	// provide a single configfile instance to all the controllers that need it
	private void initData() throws IOException {
		ConfigFile config = new ConfigFile("test");

		FXMLLoader configLoader = new FXMLLoader(getClass().getResource("config/Config.fxml"));
		configLoader.load();
		ConfigController configController = configLoader.getController();
		configController.initData(config);
	}
}
