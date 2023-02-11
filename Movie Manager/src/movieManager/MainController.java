package movieManager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * TODO: 	
 * 		- (eventually) add support for large folders (TV shows?)
 * 		- add null handling for next/prev
 * 		- process the filenames
 * 		- add "save" feature
 * 		- add save and next button
 * 		- implement checkboxes
 */

public class MainController implements Initializable {

	/**
	 *  UI ELEMENTS
	 */
	
	// Source directory elements
	@FXML private TextField sourceDirTextField;
	@FXML private Label sourceDirStatus;
	
	// Table elements
	@FXML private TableView<FileView> fileTable;
	@FXML private TableColumn<FileView, String> nameCol;
	@FXML private TableColumn<FileView, String> typeCol;
	@FXML private TableColumn<FileView, String> contentsCol;
	
	// File processor elements
	@FXML private TextField editedNameField;
	@FXML private Label origNameLabel;
	@FXML private Label folderNameLabel;
	
	/**
	 *  LOCAL VARIABLES
	 */
	
	File directory;
	final String BERTHA_PATH = "D:\\Torrents";
	
	List<File> dirFiles;
	int dirIndex = 0;
	
	File currentFile;
	File parentDir;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
        nameCol.setCellValueFactory(new PropertyValueFactory<FileView, String>("name"));
        typeCol.setCellValueFactory(new PropertyValueFactory<FileView, String>("type"));
        contentsCol.setCellValueFactory(new PropertyValueFactory<FileView, String>("contents"));
	}
	
	public void loadBerthaSourceDir() {
        load(BERTHA_PATH);
	}
	
	public void loadTextFieldSourceDir() {
		load(sourceDirTextField.getText());
	}
	
	public void sourceDirKeyPress(KeyEvent e) {
		if (e.getCode().equals(KeyCode.ENTER)) {
			load(sourceDirTextField.getText());
        }
	}
	
	// initialize the tool once the directory is specified
	private void load(String path) {
		if(!loadDirectory(path))
			return;
		
		dirFiles = Arrays.asList(directory.listFiles());
		
		loadTable();
		loadFile();
	}
	
	// populate the "directory" variable and make sure it's valid
	private boolean loadDirectory(String path) {
		File dir = new File(path);
		
		if(!dir.exists()) {
			sourceDirStatus.setText("Invalid or non-existant path provided");
			return false;
		}
		if(!dir.isDirectory()) {
			sourceDirStatus.setText("Provided path is not a directory");
			return false;
		}

		sourceDirStatus.setText("Success! This dir contains " + dir.list().length + " files");
		sourceDirTextField.setText(path);
		directory = dir;
		dirIndex = 0;
		
		return true;
	}
	
	// populate the file table with data about the files in the currently loaded directory
	private void loadTable() {
		if(directory == null) {
			throw new IllegalStateException("directory is null!");
		}
		
		List<FileView> files = new ArrayList<FileView>();
		
		for(File f : dirFiles) {
			String type;
			String contents = "";
			
			if(f.isDirectory()) {
				type = "Dir";
				
				for(String subFileName : f.list()) {
					if(subFileName.endsWith(".srt")) {
						subFileName = "<subs>.srt";
					}
					
					if(!contents.isEmpty())
						contents += ", ";
					
					contents += subFileName;
				}
			}
			else if(f.isFile()) {
				type = "File";
			}
			else type = "Unk";

			files.add(new FileView(f.getName(), type, contents));
		}
		
		fileTable.getItems().setAll(files);
		fileTable.sort();
	}
	
	// load the file at the current index
	private void loadFile() {
		File file = dirFiles.get(dirIndex);
		
		if(file.isDirectory()) {
			// remove support for large directories
			if(file.list().length > 2) {
				nextFile();
				return;
			}
	
			currentFile = getMovieFileFromDir(file);
			parentDir = file;
		}
		else {
			currentFile = file;
			parentDir = null;
		}

		String filename = currentFile.getName();
		
		editedNameField.setText(processFilename(filename));
		origNameLabel.setText("Original: " + filename);
		folderNameLabel.setText(parentDir == null ? "" : "Folder: " + parentDir.getName());
	}
	
	// assess a folder and retrieve the media file
	private File getMovieFileFromDir(File dir) {
		File[] contents = dir.listFiles();
		
		if(contents.length == 1) {
			System.out.println(contents[0].getName());
			return contents[0];
		}
		else if(contents.length == 2) {
			// TODO this ignores the case where a folder contains 2 files but no srt
			if(contents[0].getName().endsWith(".srt"))
				return contents[1];
			if(contents[1].getName().endsWith(".srt"))
				return contents[0];
		}
		
		throw new IllegalStateException("No oversized dirs! Arr:" + Arrays.toString(contents));
		
//		List<File> sortedContents = Arrays.asList(contents);
//		sortedContents.sort(new Comparator<File>() {
//			@Override
//			public int compare(File one, File two) {
//				String nameOne = one.getName();
//				String nameTwo = two.getName();
//				
//				// de-prioritize subtitle files
//				if(nameOne.endsWith(".srt"))
//					return 1;
//				if(nameTwo.endsWith(".srt"))
//					return -1;
//				
//				// prioritize period-delimited names
//				int numPeriodsOne = nameOne.length() - nameOne.replace(".", "").length();
//				int numPeriodsTwo = nameTwo.length() - nameTwo.replace(".", "").length();
//				if(numPeriodsOne < numPeriodsTwo)
//					return -1;
//				if(numPeriodsTwo < numPeriodsOne)
//					return 1;
//				
//				// leave it up to length I guess
//				if(nameOne.length() < nameTwo.length())
//					return 1;
//				if(nameTwo.length() < nameOne.length())
//					return -1;
//				
//				return 0;
//			}
//		});
//		
//		//TODO remove
//		System.out.println(sortedContents.toString());
//		
//		return sortedContents.get(0);
	}
	
	// edit a file name
	private String processFilename(String filename) {
		//TODO
		return filename;
	}
	
	public void nextFile() {
		if(dirIndex == dirFiles.size() - 1) {
			sourceDirStatus.setText("End of the files reached");
			return;
		}
		
		dirIndex++;
		sourceDirStatus.setText("");
		loadFile();
	}
	
	public void prevFile() {
		if(dirIndex == 0) {
			sourceDirStatus.setText("Start of the files reached");
			return;
		}

		dirIndex--;
		sourceDirStatus.setText("");
		loadFile();
	}
}