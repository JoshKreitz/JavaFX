package movieManager.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// bean-style data class to sum up all the metadata a movie has
public class MovieMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	private int movieId;
	private String title;
	private String releaseDate;
	private String description;
	private String imagePath;
	private List<String> genres;

	private long metadataCreationDate;

	public MovieMetadata() {
		movieId = -1;
		title = "Unavailable";
		releaseDate = "01-01-1970";
		description = "This movie's data has not been downloaded yet or is unavailable.";
		imagePath = "file:images/photo.JPG";
		genres = new ArrayList<String>();
		metadataCreationDate = System.currentTimeMillis();
	}

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
