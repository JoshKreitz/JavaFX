package movieManager.metadata;

import java.util.List;

import javafx.scene.image.Image;

// bean-style data class to sum up all the metadata a movie has
public class MovieMetadata {

	private int movieId;
	private String title;
	private String releaseDate;
	private String description;
	private List<String> genres;

	private long metadataCreationDate;

	public MovieMetadata() {
		metadataCreationDate = System.currentTimeMillis();
	}

	public MovieMetadata(int movieId, String title, String releaseDate, String description, List<String> genres) {
		this.movieId = movieId;
		this.title = title;
		this.releaseDate = releaseDate;
		this.description = description;
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
}
