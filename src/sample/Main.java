package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage window) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        window.setTitle("Movie List");

        window.setScene(new Scene(root, 800, 500));
        window.show();
    }


}
