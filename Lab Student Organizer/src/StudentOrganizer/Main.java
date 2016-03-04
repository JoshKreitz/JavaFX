package StudentOrganizer;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//Main runner class. Starts off the program, that's it
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    //Property for the title of the main window, set from MainWindowController.java in the lookupStudent method. The title changes to display the currently viewed student
    public static StringProperty title = new SimpleStringProperty("Student Lookup");

    @Override
    public void start(Stage window) throws Exception {
        {
            //connects the MainWindow fxml file
            Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
            window.titleProperty().bind(title);

            window.setMinHeight(250);

            window.setScene(new Scene(root, 500, 600));
            window.show();
        }
    }
}
