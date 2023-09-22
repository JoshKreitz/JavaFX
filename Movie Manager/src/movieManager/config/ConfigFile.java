package movieManager.config;

public class ConfigFile {
	
	private String fileManagerDir;
	private String shelfDir;
	
	public ConfigFile(String tmp) {
		// TODO load file
		fileManagerDir = tmp;
	}
	
	public void saveFile(String fileManagerDir, String shelfDir) {
		// TODO
	}
	
	public String getFileManagerDir() {
		return fileManagerDir;
	}
	
	public String getShelfDir() {
		return shelfDir;
	}
}
