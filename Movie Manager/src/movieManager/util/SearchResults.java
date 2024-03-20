package movieManager.util;

import java.util.List;

/**
 * A simple bean to parse the JSON response returned from a query to the movie
 * API
 */
public class SearchResults {
	int page;
	int total_results;
	int total_pages;
	List<SearchMovie> results;

	public SearchResults() {
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotal_results() {
		return total_results;
	}

	public void setTotal_results(int total_results) {
		this.total_results = total_results;
	}

	public int getTotal_pages() {
		return total_pages;
	}

	public void setTotal_pages(int total_pages) {
		this.total_pages = total_pages;
	}

	public List<SearchMovie> getResults() {
		return results;
	}

	public void setResults(List<SearchMovie> results) {
		this.results = results;
	}

	public int getNumResults() {
		return results != null ? results.size() : 0;
	}

	@Override
	public String toString() {
		return String.format("SearchResults[page=%d,total_results=%d,total_pages=%d,results.size=%d]", page,
				total_results, total_pages, results.size());
	}
}
