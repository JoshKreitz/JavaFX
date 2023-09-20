package movieManager;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import movieManager.fileManager.FileManagerController;
import movieManager.movieShelf.ShelfController;

public class RootController {
	@FXML private TabPane tabPane;

	@FXML private Tab fileManagerTab;
	@FXML private FileManagerController fileManagerController;

	@FXML private Tab movieShelfTab;
	@FXML private ShelfController movieShelfController;

	public void init() {
		tabPane.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
					if (newValue == fileManagerTab) {
						System.out.println("File Manager Tab");
						System.out.println("fileManagerController=" + fileManagerController);

						// xxx_tab2bar_xxxController.handleTab2ButtonBar();
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
