package BasicJavaFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//Main runner class. Starts off the program, that's it
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) throws Exception {
        {
            //connects the MainWindow fxml file
            Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
            window.setTitle("DMV Waitlist");

            //stops the user from resizing the window, I didn't want to deal with image resizing for this project
            window.setResizable(false);

            //window.setScene(new Scene(root, 800, 600));
            window.setScene(new Scene(root, 1000, 600));
            window.show();
        }
    }
}
