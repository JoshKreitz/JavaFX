package PhysicsEngine;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sun.awt.WindowClosingListener;

import java.awt.event.WindowEvent;

//Main runner class. Starts off the program, that's it
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    //properites accessed by the canvas to allow it to resize
    static ReadOnlyDoubleProperty windowHeight;
    static ReadOnlyDoubleProperty windowWidth;

    @Override
    public void start(Stage window) throws Exception {
        {
            //connects properties to the height and width of the window
            windowHeight = window.heightProperty();
            windowWidth = window.widthProperty();

            //connects the MainWindow fxml file
            Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
            window.setTitle("Physics Engine");

            //stops all of the threads before the program exits, which prevents the java process from continuing to run
            window.setOnCloseRequest(e -> {
                MainWindowController.stopMainThread();
                MainWindowController.stopMouseTracker();
            });

            window.setScene(new Scene(root, 1000, 600));
            window.show();
        }
    }
}
