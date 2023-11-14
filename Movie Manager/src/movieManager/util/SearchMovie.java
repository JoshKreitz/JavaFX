package movieManager.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A simple bean to parse a single movie result from the JSON response returned
 * by a query to the movie API
 */
@JsonIgnoreProperties({ "video", "vote_average", "vote_count" })
public class SearchMovie {

	/*
	 * { "adult": false, "backdrop_path": "/2WgieNR1tGHlpJUsolbVzbUbE1O.jpg",
	 * "genre_ids": [ 10752, 28, 18 ], "id": 530915, "original_language": "en",
	 * "original_title": "1917", "overview":
	 * "At the height of the First World War, two young British soldiers must cross enemy territory and deliver a message that will stop a deadly attack on hundreds of soldiers."
	 * , "popularity": 39.792, "poster_path": "/iZf0KyrE25z1sage4SYFLCCrMi9.jpg",
	 * "release_date": "2019-12-25", "title": "1917", "video": false,
	 * "vote_average": 7.991, "vote_count": 11471 }
	 */

	private int id;
	String backdrop_path;
	String poster_path;
	List<Integer> genre_ids;
	String title;
	String original_title;
	String original_language;
	String overview;
	boolean adult;
	String release_date;
	double popularity;

	public SearchMovie() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBackdrop_path() {
		return backdrop_path;
	}

	public void setBackdrop_path(String backdrop_path) {
		this.backdrop_path = backdrop_path;
	}

	public String getPoster_path() {
		return poster_path;
	}

	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}

	public List<Integer> getGenre_ids() {
		return genre_ids;
	}

	public void setGenre_ids(List<Integer> genre_ids) {
		this.genre_ids = genre_ids;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOriginal_title() {
		return original_title;
	}

	public void setOriginal_title(String original_title) {
		this.original_title = original_title;
	}

	public String getOriginal_language() {
		return original_language;
	}

	public void setOriginal_language(String original_language) {
		this.original_language = original_language;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public boolean isAdult() {
		return adult;
	}

	public void setAdult(boolean adult) {
		this.adult = adult;
	}

	public String getRelease_date() {
		return release_date;
	}

	public void setRelease_date(String release_date) {
		this.release_date = release_date;
	}

	public double getPopularity() {
		return popularity;
	}

	public void setPopularity(double popularity) {
		this.popularity = popularity;
	}

	@Override
	public String toString() {
		return String.format("SearchMovie[title=%s,release_date=%s]", title, release_date);
	}

}
