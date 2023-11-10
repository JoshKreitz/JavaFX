package movieManager.metadata;

import java.util.List;

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

}
