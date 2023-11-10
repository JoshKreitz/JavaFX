package movieManager.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import movieManager.ConfigFile;

public class MetadataManager {

	private final String METADATA_FOLDER_NAME = ".MovieManagerMetadata";

	private Map<String, MovieMetadata> metadata;

	private ConfigFile config;
	private Serializer<String, MovieMetadata> serializer;
	private NetworkHandler networkHandler;

	private String metadataFolderPath;

	public MetadataManager(ConfigFile config, NetworkHandler networkHandler) {
		this.config = config;
		this.networkHandler = networkHandler;

		loadMetadata();
	}

	// for when the shelf dir changes
	public void reloadMetadata() {
		loadMetadata();
	}

	// reads in the file structure, creating it if necessary
	private void loadMetadata() {
		metadataFolderPath = ConfigFile.normalizeTrailingSlash(config.getShelfDir() + METADATA_FOLDER_NAME);

		createMetadataFolder();
		serializer = new Serializer<String, MovieMetadata>(metadataFolderPath);

		loadFilenameIndex();
		loadFiles();
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
		metadata = serializer.readSerializedMap();
		if (metadata == null) {
			metadata = new HashMap<String, MovieMetadata>();
		}

		// add exit shutdown hook to save the map before closing the application
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (!metadata.isEmpty())
				serializer.saveSerializedMap(metadata);
		}));

	}

	// load all the file names from the shelf directory
	private void loadFiles() {
		Set<String> existingMetadataFiles = metadata.keySet();
		Set<String> missingMetadataFiles = new HashSet<String>();

		File[] shelfDirFiles = new File(config.getShelfDir()).listFiles();

		for (File f : shelfDirFiles) {
			// skip directories and... non-existent files?
			if (!f.exists() || f.isDirectory())
				continue;

			String filename = f.getName();
			if (filename.endsWith(".srt"))
				continue;
			
			// remove file extension
			filename = filename.substring(0, filename.lastIndexOf(".")).trim();
			
			if (!existingMetadataFiles.contains(filename)
					|| isOlderThanOneYear(metadata.get(filename).getMetadataCreationDate())
					|| metadata.get(filename).isDefault()) {
				metadata.remove(filename);
				missingMetadataFiles.add(filename);

				metadata.put(filename, new MovieMetadata());
			}
		}

		removeStaleMetadata(shelfDirFiles);
		downloadMetadata(missingMetadataFiles);
	}

	private void removeStaleMetadata(File[] shelfDirFiles) {
		Set<String> filenames = Arrays.stream(shelfDirFiles).map(File::getName).collect(Collectors.toSet());
		metadata.keySet().removeIf((s) -> !filenames.contains(s));
	}

	public Map<String, MovieMetadata> getAllMetadata() {
		return metadata;
	}

	public MovieMetadata getMetadata(String filename) {
		return metadata.get(filename);
	}

	public void addMetadata(String filename, MovieMetadata data) {
		metadata.put(filename, data);
	}

	private static boolean isOlderThanOneYear(long pastTimestampMillis) {
		long timeDifferenceMillis = System.currentTimeMillis() - pastTimestampMillis;
		long oneYearMillis = 365L * 24 * 60 * 60 * 1000; // Approximation, doesn't account for leap years

		return timeDifferenceMillis > oneYearMillis;
	}

	// asynchronously populate data for all the movies, update existing metadata
	// entries
	private void downloadMetadata(Set<String> filenames) {
		System.out.println(filenames);
		networkHandler.downloadMovies(metadata, filenames);
	}

}
