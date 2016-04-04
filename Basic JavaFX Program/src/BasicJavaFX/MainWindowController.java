package BasicJavaFX;

import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/*
        implementing Initializable allows the method "initialize" to be called when the program starts. This method
        happens after the FXML defines it's own components, thus why the button added in this example appears at the
        bottom of the screen.
 */
public class MainWindowController implements Initializable {

    public VBox MainVBox;
    public HBox RandHBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Button btn = new Button("Button added via Java code");
        MainVBox.getChildren().add(btn);

        RandHBox.setStyle("-fx-background-color: DAE6F3;");

    }

    public void setSpacingToTen() {
        MainVBox.setSpacing(10);
    }

    public void setMarginsToTen() {
        //top right bottom left
        MainVBox.setPadding(new Insets(10, 10, 10, 10));
    }
}
