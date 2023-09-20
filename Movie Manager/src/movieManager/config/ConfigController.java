package movieManager.config;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class ConfigController implements Initializable {

	@FXML private TextArea stderrTextArea;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		redirectSystemErrToTextArea();
	}

	private void redirectSystemErrToTextArea() {
		PrintStream ps = new PrintStream(new ConsoleOutputStream(), true);
		System.setErr(ps);
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
