package movieManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class ConfigFile {

	private final String CONFIG_FILE_LOCATION = System.getProperty("user.home") + "\\MovieManagerConfiguration.txt";
	private final File CONFIG_FILE = new File(CONFIG_FILE_LOCATION);

	private final String FILE_MANAGER_DIR_NAME = "FILE_MANAGER_DIR";
	private final String SHELF_DIR_NAME = "SHELF_DIR";
	private final String DELIMITER = ":";

	private Map<String, String> fileContents;

	public ConfigFile() {
		fileContents = new HashMap<String, String>();

		try {
			CONFIG_FILE.createNewFile();

			if (!CONFIG_FILE.exists() || CONFIG_FILE.isDirectory()) {
				throw new FileSystemException("Config file missing or malformed: " + CONFIG_FILE_LOCATION);
			}

			updateFileContents();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	private void updateFileContents() throws IOException, ParseException {
		try (BufferedReader br = new BufferedReader(new FileReader(CONFIG_FILE))) {
			for (String line; (line = br.readLine()) != null;) {
				parseFileLine(line);
			}
		}
	}

	private void parseFileLine(String line) throws ParseException {
		if (line.isEmpty()) {
			return;
		}

		if (!line.contains(DELIMITER)) {
			throw new ParseException("No delimiter found on line: " + line, 0);
		}

		int delimIndex = line.indexOf(DELIMITER);
		fileContents.put(line.substring(0, delimIndex), line.substring(delimIndex + 1));
	}

	public void saveFile() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(CONFIG_FILE.getAbsoluteFile()))) {
			for (String key : fileContents.keySet()) {
				bw.write(key + DELIMITER + fileContents.get(key) + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getFileManagerDir() {

		return fileContents.containsKey(FILE_MANAGER_DIR_NAME) ? fileContents.get(FILE_MANAGER_DIR_NAME) : "";
	}

	public void setFileManagerDir(String dir) {
		fileContents.put(FILE_MANAGER_DIR_NAME, normalizeTrailingSlash(dir));
	}

	public String getShelfDir() {
		return fileContents.containsKey(SHELF_DIR_NAME) ? fileContents.get(SHELF_DIR_NAME) : "";
	}

	public void setShelfDir(String dir) {
		fileContents.put(SHELF_DIR_NAME, normalizeTrailingSlash(dir));
	}
	
	public static String normalizeTrailingSlash(String dir) {
		// add trailing slash if necessary
		if (dir.contains("/") && !dir.endsWith("/"))
			dir += "/";
		else if (dir.contains("\\") && !dir.endsWith("\\"))
			dir += "\\";
		
		return dir;
	}
}
