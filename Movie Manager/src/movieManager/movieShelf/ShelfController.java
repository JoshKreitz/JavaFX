package movieManager.movieShelf;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import movieManager.ConfigFile;

public class ShelfController implements Initializable {

	private ConfigFile config;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}
	
	public void initData(ConfigFile config) {
		if (this.config != null) {
			throw new IllegalStateException("ConfigFile can only be initialized once");
		}
		
		this.config = config;
	}

}
