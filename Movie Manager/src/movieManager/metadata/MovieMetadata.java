package movieManager.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple bean-style data class to contain all of the metadata for a single
 * movie.
 */
public class MovieMetadata implements Serializable {
	// a path to the default photo to be used in the absence of any others
	private final String DEFAULT_IMAGE_PATH = "images/photo.JPG";

	// a version string to prevent serialization issues when this class is modified
	private static final long serialVersionUID = 1L;

	// a simple UUID for this movie
	private int movieId;

	// movie specific details
	private String title;
	private String releaseDate;
	private String description;
	private String imagePath;
	private List<String> genres;

	// the specific time, in milliseconds, since this metadata was retrieved
	private long metadataCreationDate;

	/**
	 * Create a metadata object with default parameters
	 */
	public MovieMetadata() {
		movieId = -1;
		title = "Unavailable";
		releaseDate = "01-01-1970";
		description = "This movie's data has not been downloaded yet or is unavailable.";
		imagePath = "file:" + DEFAULT_IMAGE_PATH;
		genres = new ArrayList<String>();
		metadataCreationDate = System.currentTimeMillis();
	}

	/**
	 * Create a metadata object for a specific movie
	 * 
	 * @param movieId
	 * @param title
	 * @param releaseDate
	 * @param description
	 * @param imagePath
	 * @param genres
	 */
	public MovieMetadata(int movieId, String title, String releaseDate, String description, String imagePath,
			List<String> genres) {
		this.movieId = movieId;
		this.title = title;
		this.releaseDate = releaseDate;
		this.description = description;
		this.imagePath = imagePath;
		this.genres = genres;

		this.metadataCreationDate = System.currentTimeMillis();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

	public long getMetadataCreationDate() {
		return metadataCreationDate;
	}

	public void setMetadataCreationDate(long metadataCreationDate) {
		this.metadataCreationDate = metadataCreationDate;
	}

	public int getMovieId() {
		return movieId;
	}

	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public boolean isDefault() {
		return this.equals(new MovieMetadata());
	}

	@Override
	public String toString() {
		return String.format("MovieMetadata[%d,%s,%s,%s,%s]", movieId, title, releaseDate, description,
				genres.toString());
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof MovieMetadata)) {
			return false;
		}

		MovieMetadata other = (MovieMetadata) o;
		return movieId == other.getMovieId() && title.equals(other.getTitle())
				&& releaseDate.equals(other.getReleaseDate()) && description.equals(other.getDescription())
				&& imagePath.equals(other.getImagePath());
	}
}
