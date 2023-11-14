package movieManager.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import movieManager.util.SearchMovie;

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
	private StringProperty title = new SimpleStringProperty();
	private StringProperty releaseDate = new SimpleStringProperty();
	private StringProperty description = new SimpleStringProperty();
	private StringProperty imagePath = new SimpleStringProperty();
	private ListProperty<String> genres = new SimpleListProperty<String>();

	// the specific time, in milliseconds, since this metadata was retrieved
	private long metadataCreationDate;

	/**
	 * Create a metadata object with default parameters
	 */
	public MovieMetadata() {
		movieId = -1;
		title.set("Unavailable");
		;
		releaseDate.set("01-01-1970");
		description.set("This movie's data has not been downloaded yet or is unavailable.");
		imagePath.set("file:" + DEFAULT_IMAGE_PATH);
		metadataCreationDate = System.currentTimeMillis();
	}

	public MovieMetadata(MovieFile file) {
		this();
		this.title.set(file.getTitle());
		if (!file.getYear().isEmpty())
			this.releaseDate.set(file.getYear());
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
		this.title.set(title);
		this.releaseDate.set(releaseDate);
		this.description.set(description);
		this.imagePath.set(imagePath);
		this.genres.removeAll();
		this.genres.addAll(genres);

		this.metadataCreationDate = System.currentTimeMillis();
	}

	public MovieMetadata(SearchMovie other) {
		this();
		update(other);
	}

	public void update(SearchMovie other) {
		title.set(other.getTitle());
		releaseDate.set(other.getRelease_date());
		description.set(other.getOverview());
		// TODO add here other fields for MoviePane, such as genres
	}

	public String getTitle() {
		return title.get();
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	public String getReleaseDate() {
		return releaseDate.get();
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate.set(releaseDate);
	}

	public String getDescription() {
		return description.get();
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres.removeAll();
		this.genres.addAll(genres);
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
		return imagePath.get();
	}

	public void setImagePath(String imagePath) {
		this.imagePath.set(imagePath);
	}

	public boolean isDefault() {
		return this.equals(new MovieMetadata());
	}

	public ListProperty<String> getGenresProperty() {
		return genres;
	}

	public StringProperty getTitleProperty() {
		return title;
	}

	public StringProperty getReleaseDateProperty() {
		return releaseDate;
	}

	public StringProperty getDescriptionProperty() {
		return description;
	}

	public StringProperty getImagePathProperty() {
		return imagePath;
	}

	@Override
	public String toString() {
		return String.format("MovieMetadata[%d,%s,%s,%s]", movieId, title, releaseDate, genres.toString());
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
