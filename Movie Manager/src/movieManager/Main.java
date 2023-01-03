package movieManager;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
    	Parent fxmlLoader = FXMLLoader.load(getClass().getResource("Main.fxml"));
        
    	primaryStage.setTitle("Movie Manager");

    	primaryStage.setMinHeight(200);
    	primaryStage.setMinWidth(625);
    	primaryStage.setMaximized(false);

    	Scene scene = new Scene(fxmlLoader, 1000, 700);
    	scene.getStylesheets().add(Main.class.getResource("Main.css").toExternalForm());
    	
    	primaryStage.setScene(scene);
    	primaryStage.show();
    }
}

