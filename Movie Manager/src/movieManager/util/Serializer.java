package movieManager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

/**
 * A class to handle saving and loading the metadata in a specific directory
 * using a Map, typically mapping filenames to their corresponding metadata
 * 
 * @param <A> The key type for the metadata map
 * @param <B> The value type for the metadata map
 */
public class Serializer<A, B> {
	// the basic name of the save file
	private final String FILENAME_MAP_FILENAME = "filenameIds.ser";

	// the fully qualified filename for the save file
	private String filename;

	/**
	 * Load the target directory
	 * 
	 * @param rootDir the directory in which to save/load the metadata file
	 */
	public Serializer(String rootDir) {
		this.filename = rootDir += FILENAME_MAP_FILENAME;
	}

	/**
	 * Deserialize (read) the save file, if it exists
	 * 
	 * @return the metadata map, or null if it doesn't exist/cannot parse
	 */
	@SuppressWarnings("unchecked")
	public Map<A, B> readSerializedMap() {
		File file = new File(filename);
		if (!file.exists()) {
			System.out.println("Serial file does not exist");
			return null;
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			return (Map<A, B>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error reading the object: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Serialize (write) the metadata map to the save file
	 * 
	 * @param map the metadata map
	 */
	public void saveSerializedMap(Map<A, B> map) {
		backupFile();

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
			oos.writeObject(map);
			System.out.println("Object saved successfully!");
		} catch (IOException e) {
			System.out.println("Error saving the serial file: " + e.getMessage());
		}
	}

	/**
	 * Create a backup of the save file before writing to it, giving a single chance
	 * to recover the metadata in the event of a failure
	 */
	private void backupFile() {
		Path file = Paths.get(filename);
		if (!Files.exists(file))
			return;

		int dotIndex = filename.lastIndexOf('.');
		String backupFilename;
		if (dotIndex > 0) {
			backupFilename = filename.substring(0, dotIndex) + "-BACKUP" + filename.substring(dotIndex);
		} else {
			backupFilename = filename + "-BACKUP";
		}

		try {
			Files.copy(file, Paths.get(backupFilename), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("Failed to make a backup of the serial file!");
			e.printStackTrace();
		}
	}
}
