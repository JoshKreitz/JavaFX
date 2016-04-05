package BasicJavaFX;

import javafx.fxml.FXML;
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

    /*
        Here is one of the most powerful features of JavaFX, connecting the FXML file to Java code. These variables,
        MainVBox and RandHBox are direct representations of those components in the FXML file, except they can be used
        like a normal Java variable. These variables are automatically instantiated, and must be either public or use
        the @FXML tag (shown below). When these variables change, the FXML version does as well, and visa versa.
    */
    public VBox MainVBox;

    @FXML
    private HBox RandHBox;

    /*
        This is the initialize method, called when the window first appears. It is only called once.
    */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    /*
        Here a button is added via Java code alone, no FXML involved (except the creation of MainVBox). A Button is
        created (make sure you're importing the JavaFX Button class and not something else) and added to the MainVBox.
     */
        Button btn = new Button("Button added via Java code");
        MainVBox.getChildren().add(btn);

     /*
        Here the background colour of the HBox is set using a snippet of Css code. This is purely aesthetic, and
        JavaFX can be used efficiently without any Css at all.
      */
        RandHBox.setStyle("-fx-background-color: DAE6F3;");
    }

    /*
        These two methods are called when their specific buttons are pressed. This is established via the "onAction"
        tag in MainWindow.fxml. Note that these are zero-parameter void methods.
     */
    public void setSpacingToTen() {
        MainVBox.setSpacing(10);
    }

    public void setMarginsToTen() {
        //top right bottom left
        MainVBox.setPadding(new Insets(10, 10, 10, 10));
    }
}
