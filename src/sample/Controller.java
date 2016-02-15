package sample;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Controller {

    public void addMovie(){
        Stage popout = new Stage();
        popout.initModality(Modality.APPLICATION_MODAL);
        popout.setTitle("Add Movie");

        VBox layout = new VBox();

        Scene scene = new Scene(layout, 250,250);
        popout.setScene(scene);
        popout.showAndWait();
    }

}
