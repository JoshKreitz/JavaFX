package movieManager.fileManager;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import movieManager.config.ConfigFile;

/**
 * This tab contains the File Manager, a tool to easily rename files, squash
 * their folders, and move them around as desired.
 * 
 * TODO: - (eventually) add support for large folders (TV shows?)
 */
public class FileManagerController implements Initializable {
	/**
	 * UI ELEMENTS
	 */

	// Source directory elements
	@FXML private TextField sourceDirTextField;
	@FXML private Label sourceDirStatus;
	@FXML private Label defaultSourceDir;

	// Table elements
	@FXML private TableView<FileView> fileTable;
	@FXML private TableColumn<FileView, String> nameCol;
	@FXML private TableColumn<FileView, String> typeCol;
	@FXML private TableColumn<FileView, String> contentsCol;

	// File processor elements
	@FXML private TextField editedNameField;
	@FXML private Label origNameLabel;
	@FXML private Label folderNameLabel;
	@FXML private CheckBox subFileCheckBox;
	@FXML private CheckBox folderSquashCheckBox;

	/**
	 * LOCAL VARIABLES
	 */

	// The parent directory that the tool is operating in
	private File directory;

	// A list of files contained within the parent directory
	private List<File> dirFiles;
	// The file currently selected
	private int dirIndex = 0;

	// The file currently being operated on
	private File currentFile;
	// The parent directory of the file being operated on, not necessarily equal to
	// the root directory
	private File parentDir;

	// Records if the current file's parent directory has been deleted
	private boolean squashedDir = false;

	// The application's config file parameters
	private ConfigFile config;

	/**
	 * Establish UI elements
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nameCol.setCellValueFactory(new PropertyValueFactory<FileView, String>("name"));
		typeCol.setCellValueFactory(new PropertyValueFactory<FileView, String>("type"));
		contentsCol.setCellValueFactory(new PropertyValueFactory<FileView, String>("contents"));

		subFileCheckBox.setIndeterminate(false);
		folderSquashCheckBox.setIndeterminate(false);
	}

	/**
	 * Establish a reference to the core config file and update a text field with
	 * the root directory from that file
	 * 
	 * @param config
	 */
	public void initData(ConfigFile config) {
		if (this.config != null) {
			throw new IllegalStateException("ConfigFile can only be initialized once");
		}

		this.config = config;
		updateDefaultSourceDir();
	}

	/**
	 * Load the directory saved in the core config file
	 */
	public void loadDefaultSourceDir() {
		load(config.getFileManagerDir());
	}

	/**
	 * Load a custom directory entered by the user
	 */
	public void loadTextFieldSourceDir() {
		load(sourceDirTextField.getText());
	}

	/**
	 * Load a custom directory entered by the user on ENTER keypress
	 */
	public void sourceDirKeyPress(KeyEvent e) {
		if (e.getCode().equals(KeyCode.ENTER)) {
			load(sourceDirTextField.getText());
		}
	}

	/**
	 * Update the default source dir label with the configured path
	 */
	public void updateDefaultSourceDir() {
		defaultSourceDir.setText(config.getFileManagerDir());
	}

	/**
	 * Initialize the tool once the directory is specified by populating the table
	 * and selecting the first file
	 * 
	 * @param path the current root directory
	 */
	private void load(String path) {
		if (!loadDirectory(path))
			return;

		loadTable();
		loadFile();
	}

	/**
	 * Populate and verify the root directory
	 * 
	 * @param path the current root directory
	 * @return true iff the path is valid and the directory is loaded properly
	 */
	private boolean loadDirectory(String path) {
		if (path.contains("\\") && !path.contains("\\\\")) {
			path.replace("\\", "/");
		}

		File dir = new File(path);

		if (!dir.exists()) {
			sourceDirStatus.setText("Invalid or non-existant path provided");
			return false;
		}
		if (!dir.isDirectory()) {
			sourceDirStatus.setText("Provided path is not a directory");
			return false;
		}

		sourceDirStatus.setText("Success! This dir contains " + dir.list().length + " files");
		sourceDirTextField.setText(path);
		directory = dir;
		dirIndex = 0;

		return true;
	}

	/**
	 * Populate the file table with data about the files in the currently loaded
	 * directory
	 */
	private void loadTable() {
		if (directory == null) {
			throw new IllegalStateException("directory is null!");
		}

		dirFiles = Arrays.asList(directory.listFiles());
		sortDirFiles();

		// parse files into FileView objects that can be displayed by the Table
		List<FileView> files = new ArrayList<FileView>();
		for (File f : dirFiles) {
			String type;
			String contents = "";

			if (f.isDirectory()) {
				type = "Dir";

				String[] subfiles = f.list();
				if (subfiles != null) {
					for (int i = 0; i < subfiles.length && i < 15; ++i) {
						String subFileName = subfiles[i];
						if (subFileName.endsWith(".srt")) {
							subFileName = "<subs>.srt";
						}

						if (!contents.isEmpty())
							contents += ", ";

						contents += subFileName;
					}
				}
			} else if (f.isFile()) {
				type = "File";
			} else
				type = "Unk";

			files.add(new FileView(f.getName(), type, contents));
		}

		// update the table with the parsed objects
		fileTable.getItems().setAll(files);
		fileTable.sort();
	}

	/**
	 * Sort the dir files first by tyle (dir/file) then by name
	 */
	private void sortDirFiles() {
		dirFiles.sort(new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				if (f1 == null)
					return 1;
				if (f2 == null)
					return -1;
				if (f1 == f2)
					return 0;

				boolean f1dir = f1.isDirectory();
				boolean f2dir = f2.isDirectory();
				if (f1dir && !f2dir)
					return -1;
				if (f2dir && !f1dir)
					return 1;

				return f1.compareTo(f2);
			}
		});
	}

	/**
	 * Load the file at the current index and prepare for it to be operated on
	 */
	private void loadFile() {
		if (dirFiles.isEmpty()) {
			return;
		}

		File file = dirFiles.get(dirIndex);

		if (file.isDirectory()) {
			currentFile = getMovieFileFromDir(file);
			parentDir = file;
		} else {
			currentFile = file;
			parentDir = null;
		}

		String filename = currentFile.getName();

		editedNameField.setText(processFilename(filename));
		origNameLabel.setText("Original: " + filename);
		folderNameLabel.setText(parentDir == null ? "" : "Folder: " + parentDir.getName());
	}

	/**
	 * Retrieve the media file from within a sub folder
	 * 
	 * @param dir the subfolder File
	 * @return a non-.srt file from within the folder, or the folder if no file is
	 *         found
	 */
	private File getMovieFileFromDir(File dir) {
		File[] contents = dir.listFiles();

		if (contents.length == 1) {
			return contents[0];
		} else if (contents.length == 2) {
			if (contents[0].getName().endsWith(".srt"))
				return contents[1];
			if (contents[1].getName().endsWith(".srt"))
				return contents[0];
		}

		// TODO fill out implementation for folders with >2 items, or two items with no
		// SRT.
		return dir;
	}

	/**
	 * Make a best-effort attempt to properly format a file name by replacing
	 * period-delimited names with spaces and parsing out release dates or
	 * resolutions.
	 * 
	 * @param original_filename The original filename
	 * @return a best-effort parsed filename
	 */
	private String processFilename(String original_filename) {
		String trimmed_filename = stripFileType(original_filename);

		// attempt to parse period-delimited filenames
		if (trimmed_filename.contains(".")) {
			String[] parts = trimmed_filename.split("[.]");
			String year = "";
			String res = "";

			// attempt to identify year and resolution
			for (String s : parts) {
				if (s.isEmpty())
					continue;

				// check for a year surrounded by parenthesis and remove them
				if (s.length() == 6 && s.charAt(0) == '(' && s.charAt(5) == ')' && Character.isDigit(s.charAt(1))) {
					s = s.substring(1, 4);
				}
				// check for year field by looking for 4 digit number
				if (s.length() == 4) {
					if (!Character.isDigit(s.charAt(0)))
						continue;

					try {
						Integer.parseInt(s);
						year = s;
					} catch (NumberFormatException e) {
					}
				}
				// check for resolution field by looking for p and a number
				if (s.toLowerCase().charAt(s.length() - 1) == 'p' && (s.length() == 4 || s.length() == 5)) {
					if (!Character.isDigit(s.charAt(0)))
						continue;

					try {
						Integer.parseInt(s.substring(0, s.length() - 2));
						res = s;
					} catch (NumberFormatException e) {
					}
				}
			}

			// assemble the new filename
			String new_filename = "";
			for (String s : parts) {
				if (!year.isEmpty() && s.equals(year)) {
					new_filename += " (" + year + ")" + (res.isEmpty() ? "" : " " + res);
					break;
				} else if (new_filename.isEmpty())
					new_filename += s;
				else
					new_filename += " " + s;
			}

			return new_filename + "." + getFileType(original_filename);
		}

		return original_filename;
	}

	/**
	 * Move to the next file in the directory list
	 */
	public void nextFile() {
		if (dirFiles == null || dirFiles.size() == 0) {
			return;
		}

		if (dirIndex == dirFiles.size() - 1) {
			sourceDirStatus.setText("End of the files reached");
			return;
		}

		dirIndex++;
		sourceDirStatus.setText("");
		squashedDir = false;
		loadFile();
	}

	/**
	 * Move to the previous file in the directory list
	 */
	public void prevFile() {
		if (dirFiles == null || dirFiles.size() == 0) {
			return;
		}

		if (dirIndex == 0) {
			sourceDirStatus.setText("Start of the files reached");
			return;
		}

		dirIndex--;
		sourceDirStatus.setText("");
		loadFile();
	}

	/**
	 * Save the file name edited/modified by the user
	 * 
	 * @throws IOException if the file failes to save
	 */
	public void saveFile() throws IOException {
		if (!currentFile.exists() || (parentDir != null && !parentDir.exists())) {
			return;
		}

		String filename = editedNameField.getText();
		String typeStrippedNewFilename = stripFileType(filename);
		String typeStrippedOriginalFilename = stripFileType(currentFile.getName());

		// update the subtitle file name
		if (subFileCheckBox.isSelected()) {
			// search for a file with the exact same name
			File parent = parentDir != null ? parentDir : directory;
			for (File f : parent.listFiles()) {
				if (!f.equals(currentFile) && stripFileType(f.getName()).equals(typeStrippedOriginalFilename)) {
					renameFile(f, typeStrippedNewFilename + "." + getFileType(f.getName()));
				}
			}
		}

		renameFile(currentFile, filename);

		// squash the folder if selected
		// TODO will not squash folders with more than two subfiles
		if (folderSquashCheckBox.isSelected() && parentDir != null && parentDir.exists()
				&& parentDir.list().length <= 2) {

			for (File f : parentDir.listFiles()) {
				Files.move(Path.of(f.getAbsolutePath()), Path.of(directory.getAbsolutePath()).resolve(f.getName()),
						StandardCopyOption.REPLACE_EXISTING);
			}

			parentDir.delete();
			squashedDir = true;
		}

		loadTable();
		loadFile();
	}

	/**
	 * Remove the file type and period from a filename
	 * 
	 * @param filename
	 * @return the filename without a trailing type
	 */
	private String stripFileType(String filename) {
		int lastIndex = filename.lastIndexOf(".");

		if (lastIndex < 0)
			return filename;

		return filename.substring(0, lastIndex);
	}

	/**
	 * Parse the filetype of a file excluding the period
	 * 
	 * @param filename
	 * @return the filetype
	 */
	private String getFileType(String filename) {
		int lastIndex = filename.lastIndexOf(".");

		if (lastIndex < 0)
			return "";

		return filename.substring(lastIndex + 1);
	}

	/**
	 * Rename a file
	 * 
	 * @param f        the file to be renamed
	 * @param filename the updated filename
	 * @throws IOException if the file cannot be written to
	 */
	private void renameFile(File f, String filename) throws IOException {
		Path source = Paths.get(f.getAbsolutePath());
		Files.move(source, source.resolveSibling(filename));
	}

	/**
	 * Save the current file and move on to the next
	 * 
	 * @throws IOException if the file cannot be written to
	 */
	public void saveAndNext() throws IOException {
		saveFile();

		if (!squashedDir) {
			nextFile();
		} else {
			squashedDir = false;
		}
	}

	/**
	 * Generate a manifest of the files in the root dir and copy it to the clipboard
	 */
	public void copyManifest() {
		if (directory == null)
			return;

		String filenames = "";
		boolean seenFile = false;
		for (File f : dirFiles) {
			String name = f.getName();
			if (name.endsWith(".srt"))
				continue;

			if (!filenames.isEmpty())
				filenames += "\n";

			if (!seenFile && !f.isDirectory()) {
				seenFile = true;
				filenames += "\n";
			}

			filenames += name;
		}

		StringSelection stringSelection = new StringSelection(filenames);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}
}