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
import movieManager.ConfigFile;

public class ConfigController implements Initializable {
	@FXML private TextArea stderrTextArea;

	@FXML private TextField fileManagerDirTextField;
	@FXML private TextField shelfDirTextField;
	
	private ConfigFile config;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		redirectSystemErrToTextArea();
	}

	public void initData(ConfigFile config) {
		if (this.config != null) {
			throw new IllegalStateException("ConfigFile can only be initialized once");
		}
		
		this.config = config;
		resetFields();
	}

	public void resetFields() {
		fileManagerDirTextField.setText(config.getFileManagerDir());
		shelfDirTextField.setText(config.getShelfDir());
	}

	public void saveFields() {
		//TODO ADD VALIDATION
		config.setFileManagerDir(fileManagerDirTextField.getText());
		config.setShelfDir(shelfDirTextField.getText());
		config.saveFile();
	}
	
	private void redirectSystemErrToTextArea() {
		//TODO enable this before final build
		PrintStream ps = new PrintStream(new ConsoleOutputStream(), true);
		//System.setErr(ps);
	}
	
	public void copyErrorConsole() {
		StringSelection stringSelection = new StringSelection(stderrTextArea.getText());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}

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
