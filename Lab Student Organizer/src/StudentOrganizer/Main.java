package StudentOrganizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) throws Exception {
        {
            Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
            window.setTitle("Student Lookup");

            window.setMinHeight(250);
            //window.setMinWidth(625);
            //window.setResizable(false);

            window.setScene(new Scene(root, 300, 400));
            window.show();
        }
    }
}
