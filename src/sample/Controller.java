package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Controller {

    private Stage popout;
    public ObservableList<Movie> movies = FXCollections.observableArrayList();

    public void addMovie(){
        popout = new Stage();
        popout.initModality(Modality.APPLICATION_MODAL);
        popout.setTitle("Add Movie");

        VBox layout = new VBox();

        Scene scene = new Scene(layout, 250,250);
        popout.setScene(scene);
        popout.showAndWait();
    }

}
