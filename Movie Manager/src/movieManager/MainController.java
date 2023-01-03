package movieManager;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class MainController implements Initializable {

	/**
	 *  UI ELEMENTS
	 */
	
	// Source directory elements
	@FXML private Button loadBerthaButton;
	@FXML private Button loadSourceDirButton;
	@FXML private TextField sourceDirTextField;
	@FXML private Label sourceDirStatus;
	
	/**
	 *  LOCAL VARIABLES
	 */
	
	File directory;
	final String BERTHA_PATH = "D:\\Torrents";
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		
		
	}
	
	public void loadBerthaSourceDir() {
        loadDirectory(BERTHA_PATH);
	}
	
	public void loadTextFieldSourceDir() {
		loadDirectory(sourceDirTextField.getText());
	}
	
	public void sourceDirKeyPress(KeyEvent e) {
		if (e.getCode().equals(KeyCode.ENTER)) {
			loadDirectory(sourceDirTextField.getText());
        }
	}
	
	// populate the "directory" variable and make sure it's valid
	private void loadDirectory(String path) {
		File dir = new File(path);
		
		if(!dir.exists()) {
			sourceDirStatus.setText("Invalid or non-existant path provided");
			return;
		}
		if(!dir.isDirectory()) {
			sourceDirStatus.setText("Provided path is not a directory");
			return;
		}

		sourceDirStatus.setText("Success! This dir contains " + dir.list().length + " files");
		sourceDirTextField.setText(path);
		directory = dir;
	}
	
}