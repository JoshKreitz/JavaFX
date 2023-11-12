package movieManager.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import movieManager.config.ConfigFile;

/**
 * A key component to handle the core metadata map. This class controls the
 * lifecycle of the metadata, making sure it is loaded, saved, and managed
 * properly based on what's available.
 */
public class MetadataManager {
	// the metadata folder to contain the metadata serialization file and the cached
	// images
	private final String METADATA_FOLDER_NAME = ".MovieManagerMetadata";

	// the core metadata map from filename -> metadata
	private static Map<String, MovieMetadata> metadata;

	// references to core entities
	private ConfigFile config;
	private Serializer<String, MovieMetadata> serializer;
	private NetworkHandler networkHandler;

	/**
	 * Establish references and attempt to load the metadata from the filesystem
	 * 
	 * @param config
	 * @param networkHandler
	 */
	public MetadataManager(ConfigFile config, NetworkHandler networkHandler) {
		this.config = config;
		this.networkHandler = networkHandler;

		loadMetadata();

		// add exit shutdown hook to save the map before closing the application
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (metadata != null && !metadata.isEmpty())
				serializer.saveSerializedMap(metadata);
		}));
	}

	/**
	 * Reload the metadata from the filesystem
	 */
	public void reloadMetadata() {
		loadMetadata();
	}

	/**
	 * Attempt to load the metadata from the metadata folder, or create it if it
	 * doesn't exist
	 */
	private void loadMetadata() {
		String metadataFolderPath = ConfigFile.normalizeTrailingSlash(config.getShelfDir() + METADATA_FOLDER_NAME);

		validateMetadataFolder(metadataFolderPath);

		// load an existing metadata map from the filesystem, if it exists
		serializer = new Serializer<String, MovieMetadata>(metadataFolderPath);
		metadata = serializer.readSerializedMap();
		if (metadata == null) {
			metadata = new HashMap<String, MovieMetadata>();
		}

		updateMetadataMap();
	}

	/**
	 * Create the metadata folder if it doesn't exist
	 */
	private void validateMetadataFolder(String metadataFolderPath) {
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

	/**
	 * Refresh the metadata for all the files in the current shelf directory.
	 * Metadata is fetched if no metadata is currently loaded or the cached metadata
	 * is older than 1 year
	 */
	private void updateMetadataMap() {
		Set<String> existingMetadataFiles = metadata.keySet();
		Set<String> missingMetadataFiles = new HashSet<String>();

		File[] shelfDirFiles = new File(config.getShelfDir()).listFiles();

		for (File f : shelfDirFiles) {
			// skip directories and... non-existent files?
			if (!f.exists() || f.isDirectory())
				continue;

			// skip subtitle files
			String filename = f.getName();
			if (filename.endsWith(".srt"))
				continue;

			// remove file extension
			filename = filename.substring(0, filename.lastIndexOf(".")).trim();

			// fetch metadata for selected files
			if (!existingMetadataFiles.contains(filename)
					|| isOlderThanOneYear(metadata.get(filename).getMetadataCreationDate())
					|| metadata.get(filename).isDefault()) {
				metadata.remove(filename);
				missingMetadataFiles.add(filename);

				// TODO don't think this is necessary
				// metadata.put(filename, new MovieMetadata());
			}
		}

		removeUnassociatedMetadata(shelfDirFiles);

		// asynchronously populate data for the selected movies
		downloadMovies(missingMetadataFiles);
	}

	/**
	 * Begin the request flow to download metadata for all requested movies
	 * 
	 * @param filenames the filenames for movies that need metadata downloaded
	 */
	private void downloadMovies(Set<String> filenames) {
		filenames.stream().map(MovieFile::new)
				.forEach(movie -> NetworkHandler.downloadMovie(movie, MetadataManager::handleSearchResults));
	}

	/**
	 * A callback function to handle search results for a movie. If exactly 1 result
	 * is found, that metadata will be used. If no results were found, this function
	 * will attempt to broaden the search by removing the release year, otherwise
	 * the movie will display only default place holders. If more than one result is
	 * found, then the most popular result will be used.
	 * 
	 * @param movie         The movie that was searched
	 * @param searchResults The API search results
	 */
	public static void handleSearchResults(MovieFile movie, SearchResults searchResults) {
		int totalResults = searchResults.getTotal_results();
		// no API results were returned
		if (totalResults == 0) {
			// if the movie has a year, remove it and try to fetch it again
			if (movie.hasYear()) {
				movie.removeYear();
				NetworkHandler.downloadMovie(movie, MetadataManager::handleSearchResults);
			}
		} else {
			List<SearchMovie> results = searchResults.getResults();

			// sort results by popularity if there are more than one
			if (results.size() > 1) {
				results.sort(new Comparator<SearchMovie>() {
					@Override
					public int compare(SearchMovie o1, SearchMovie o2) {
						if (o1 == null)
							return 1;
						if (o2 == null)
							return -1;
						if (o1 == o2)
							return 0;

						double o1pop = o1.getPopularity();
						double o2pop = o2.getPopularity();
						if (o1pop < o2pop)
							return -1;
						if (o1pop > o2pop)
							return 1;

						return o1.getTitle().compareTo(o2.getTitle());
					}
				});
			}

			setMetadata(movie, results.get(0));

			// TODO kick off flow to download the image;
		}
	}

	/**
	 * Set the metadata for a single movie based on the fetched result
	 * 
	 * @param movie  The movie file for which the metadata will be updated
	 * @param result The metadata returned by the API
	 */
	private static void setMetadata(MovieFile movie, SearchMovie result) {
		MovieMetadata final_metadata = new MovieMetadata(result.getId(), result.getTitle(), result.getRelease_date(),
				result.getOverview(), "", null);
		metadata.put(movie.getFilename(), final_metadata);
	}

	/**
	 * Remove any metadata that isn't tied to the files in the shelf directory
	 * 
	 * @param shelfDirFiles A list of files in the current directory
	 */
	private void removeUnassociatedMetadata(File[] shelfDirFiles) {
		Set<String> filenames = Arrays.stream(shelfDirFiles).map(File::getName).collect(Collectors.toSet());
		metadata.keySet().removeIf((s) -> !filenames.contains(s));
	}

	/**
	 * @return the core map of metadata
	 */
	public Map<String, MovieMetadata> getAllMetadata() {
		return metadata;
	}

	/**
	 * @param filename to retrieve the metadata for
	 * @return the specified file's metadata
	 */
	public MovieMetadata getMetadata(String filename) {
		return metadata.get(filename);
	}

	/**
	 * @param filename to add metadata to
	 * @param data     The metadata
	 */
	public void addMetadata(String filename, MovieMetadata data) {
		metadata.put(filename, data);
	}

	/**
	 * Determine if a given millisecond long value is older than 1 year
	 * 
	 * @param pastTimestampMillis
	 * @return true iff the given time is older than one year
	 */
	private static boolean isOlderThanOneYear(long pastTimestampMillis) {
		long timeDifferenceMillis = System.currentTimeMillis() - pastTimestampMillis;
		long oneYearMillis = 365L * 24 * 60 * 60 * 1000; // Approximation, doesn't account for leap years

		return timeDifferenceMillis > oneYearMillis;
	}
}
