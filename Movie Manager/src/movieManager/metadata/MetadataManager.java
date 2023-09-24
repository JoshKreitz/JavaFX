package movieManager.metadata;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.openmbean.InvalidOpenTypeException;

import movieManager.ConfigFile;

public class MetadataManager {

	// establish the file structure if it doesn't exist
	// read it in if it does exist
	// maintain root data structures for movie metadata
	// provide it to UI

	private final String METADATA_FOLDER_NAME = ".MovieManagerMetadata";

	// translate filenames to Ids
	// this object will get serialized and saved to a file
	private Map<String, MovieMetadata> filenamesToIds;

	private ConfigFile config;
	private Serializer<String, MovieMetadata> serializer;

	private String metadataFolderPath;

	public MetadataManager(ConfigFile config) {
		this.config = config;

		loadMetadata();
	}

	// for when the shelf dir changes
	public void reloadMetadata() {

	}

	// reads in the file structure, creating it if necessary
	private void loadMetadata() {
		metadataFolderPath = ConfigFile.normalizeTrailingSlash(config.getShelfDir() + METADATA_FOLDER_NAME);
		
		createMetadataFolder();
		serializer = new Serializer(metadataFolderPath);

		loadFilenameIndex();
	}

	// create the folder and parents if it doesnt exist
	private void createMetadataFolder() {
		File dir = new File(metadataFolderPath);
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				throw new IllegalStateException(
						"Unable to create metadata folder, as it exists as a file: " + metadataFolderPath);
			}
		} else {
			dir.getParentFile().mkdirs();
			dir.mkdir();
		}
	}

	// loads the filename index from the serialized file, if it exists
	private void loadFilenameIndex() {
		filenamesToIds = serializer.readSerializedMap();
		if (filenamesToIds == null) {
			filenamesToIds = new HashMap<String, MovieMetadata>();
		}

		// add exit shutdown hook to save the map before closing the application
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (!filenamesToIds.isEmpty())
				serializer.saveSerializedMap(filenamesToIds);
		}));

	}

	public MovieMetadata getMetadata(int movieId) {
		return null; // TODO implement
	}

	public void saveMetadata(MovieMetadata metadata) {
		// TODO implement
	}

}
