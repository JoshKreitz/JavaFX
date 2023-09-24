package movieManager.metadata;

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

public class Serializer<A, B> {

	private final String FILENAME_MAP_FILENAME = "filenameIds.ser";

	private String filename;

	// takes in the root dir, creates the file in there
	public Serializer(String rootDir) {
		this.filename = rootDir += FILENAME_MAP_FILENAME;
	}

	// Method to deserialize (read) an object
	// returns null if error occurs
	@SuppressWarnings("unchecked")
	public Map<A, B> readSerializedMap() {
		File file = new File(filename);
		if(!file.exists()) {
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

	// Method to serialize (save) an object
	public void saveSerializedMap(Map<A, B> map) {
		backupFile();

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
			oos.writeObject(map);
			System.out.println("Object saved successfully!");
		} catch (IOException e) {
			System.out.println("Error saving the serial file: " + e.getMessage());
		}
	}

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
