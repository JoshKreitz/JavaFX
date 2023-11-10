package movieManager.config;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * This is a basic form to view/edit the core config file parameters and also
 * offer a display of the current error output stream in the event of unexpected
 * issues.
 */
public class ConfigController implements Initializable {
	/**
	 * UI ELEMENTS
	 */

	@FXML private TextArea stderrTextArea;

	@FXML private TextField fileManagerDirTextField;
	@FXML private TextField shelfDirTextField;

	/**
	 * LOCAL VARIABLES
	 */

	private ConfigFile config;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		redirectSystemErrToTextArea();
	}

	/**
	 * Establish a reference to the core config file, then populate the existing
	 * parameters from that file
	 * 
	 * @param config
	 */
	public void initData(ConfigFile config) {
		if (this.config != null) {
			throw new IllegalStateException("ConfigFile can only be initialized once");
		}

		this.config = config;
		resetFields();
	}

	/**
	 * Reset the config fields to those found in the existing config file
	 */
	public void resetFields() {
		fileManagerDirTextField.setText(config.getFileManagerDir());
		shelfDirTextField.setText(config.getShelfDir());
	}

	/**
	 * Save the user-populated config parameters to the config file
	 */
	public void saveFields() {
		// TODO ADD VALIDATION
		// TODO detect dirty and reload the metadata if the shelf dir changes
		config.setFileManagerDir(fileManagerDirTextField.getText());
		config.setShelfDir(shelfDirTextField.getText());
		config.saveFile();
	}

	/**
	 * Redirect the default error output stream to the UI
	 */
	private void redirectSystemErrToTextArea() {
		// TODO enable this before final build
		PrintStream ps = new PrintStream(new ConsoleOutputStream(), true);
		// System.setErr(ps);
	}

	/**
	 * Copy the error console to the clipboard
	 */
	public void copyErrorConsole() {
		StringSelection stringSelection = new StringSelection(stderrTextArea.getText());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}

	/**
	 * A custom output stream to update the error console asynchronously
	 */
	private class ConsoleOutputStream extends OutputStream {
		@Override
		public void write(int b) {
			appendText(String.valueOf((char) b));
		}

		@Override
		public void write(byte[] b, int off, int len) {
			appendText(new String(b, off, len));
		}

		@Override
		public void write(byte[] b) {
			write(b, 0, b.length);
		}

		private void appendText(String value) {
			Platform.runLater(() -> {
				stderrTextArea.appendText(value);
				stderrTextArea.setScrollTop(Double.MAX_VALUE); // auto-scroll
			});
		}
	}

}
