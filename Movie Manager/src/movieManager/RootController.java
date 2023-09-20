package movieManager;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import movieManager.fileManager.FileManagerController;

public class RootController {
	@FXML
	private TabPane tabPane;

	// Inject tab content
	@FXML
	private Tab fileManagerTab;

	// Inject tab controller
	@FXML
	private FileManagerController fileManagerController;

	public void init() {
		tabPane.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
					if (newValue == fileManagerTab) {
						System.out.println("File Manager Tab");
						System.out.println("fileManagerController=" + fileManagerController);

						// xxx_tab2bar_xxxController.handleTab2ButtonBar();
					} else {
						System.out.println("SOMETHING GOOFED");
					}
				});
	}

}
