package movieManager.config;

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

/**
 * Manage the core application config file, which saves any configuration
 * options that are independent of core functionality.
 */
public class ConfigFile {
	// the default file path for the config file, "\" delimited for Windows
	private final String CONFIG_FILE_LOCATION = System.getProperty("user.home") + "\\MovieManagerConfiguration.txt";
	private final File CONFIG_FILE = new File(CONFIG_FILE_LOCATION);

	// The name of the config parameter that stores the default directory for the
	// File Manager
	private final String FILE_MANAGER_DIR_NAME = "FILE_MANAGER_DIR";
	// The name of the config parameter that storwes the default directory for the
	// Shelf viewer
	private final String SHELF_DIR_NAME = "SHELF_DIR";
	// The default config parameter name:value delimiter
	private final String DELIMITER = ":";

	// A parsed representation of values read from the config file
	private Map<String, String> fileContents;

	/**
	 * Creates the config file if it doesn't already exist, or reads in it's
	 * contents if it does.
	 */
	public ConfigFile() {
		fileContents = new HashMap<String, String>();

		try {
			CONFIG_FILE.createNewFile();

			if (!CONFIG_FILE.exists() || CONFIG_FILE.isDirectory()) {
				throw new FileSystemException("Config file missing or malformed: " + CONFIG_FILE_LOCATION);
			}

			updateFileContents();
		} catch (IOException | ParseException e) {
			// TODO implement
			e.printStackTrace();
		}
	}

	/**
	 * Very basic parsing function to read in values line by line from the config
	 * file
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	private void updateFileContents() throws IOException, ParseException {
		try (BufferedReader br = new BufferedReader(new FileReader(CONFIG_FILE))) {
			for (String line; (line = br.readLine()) != null;) {
				parseFileLine(line);
			}
		}
	}

	/**
	 * Parses a single config file line using the first instance of the param
	 * delimiter
	 * 
	 * @param line The line to be parsed
	 * @throws ParseException if no delimiter is found
	 */
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

	/**
	 * Writes the current config parameters to the config file
	 */
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

	/**
	 * Adds a trailing slash to a filepath, "\" or "/" based on what's detected in
	 * the path
	 * 
	 * @param dir the filepath
	 * @return the filepath with a trailing slash
	 */
	public static String normalizeTrailingSlash(String dir) {
		// add trailing slash if necessary
		if (dir.contains("/") && !dir.endsWith("/"))
			dir += "/";
		else if (dir.contains("\\") && !dir.endsWith("\\"))
			dir += "\\";

		return dir;
	}
}
