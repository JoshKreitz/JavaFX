package movieManager;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import movieManager.config.ConfigController;
import movieManager.fileManager.FileManagerController;
import movieManager.movieShelf.ShelfController;

public class RootController implements Initializable {
	@FXML private TabPane tabPane;

	@FXML private Tab fileManagerTab;
	@FXML private FileManagerController fileManagerController;

	@FXML private Tab movieShelfTab;
	@FXML private ShelfController movieShelfController;

	@FXML private Tab configTab;
	@FXML private ConfigController configController;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tabPane.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
					if (newValue == fileManagerTab) {
						System.out.println("File Manager Tab");
						System.out.println("fileManagerController=" + fileManagerController);
					} 
					else if (newValue == movieShelfTab) {
						System.out.println("Movie Shelf Tab");
						System.out.println("movieShelfController=" + movieShelfController);
					}
					else {
						System.out.println("SOMETHING GOOFED");
					}
				});
	}

}
