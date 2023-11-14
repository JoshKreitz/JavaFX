package movieManager.metadata;

/**
 * A simple object to parse a filename and keep the extracted title associated
 * with it's release date year. This assumes the filename follows the syntax "V
 * for Vendetta (2006) 720p" where either or both of the year/resolution may be
 * absent.
 */
public class MovieFile {
	private String filename;
	private String title;
	private String year;

	// parse out the title and year
	public MovieFile(String filename) {
		this.filename = filename;

		// remove trailing "720p" or "1080p"
		filename = filename.replaceAll("\\s*(\\d{3,4}p)$", "");

		// parse the date
		int parenIndex = filename.indexOf('(');
		int parenEndingIndex;
		if (parenIndex != -1 && (parenEndingIndex = filename.indexOf(')')) != -1
				&& parenEndingIndex - parenIndex == 5) {
			try {
				String tmp = filename.substring(parenIndex + 1, parenEndingIndex);
				Integer.parseInt(tmp);
				year = tmp;
			} catch (NumberFormatException e) {
			}

			title = filename.substring(0, parenIndex).trim();
		} else {
			year = "";
			title = filename;
		}
	}

	public boolean hasYear() {
		return !year.isEmpty();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void removeYear() {
		this.year = "";
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String toString() {
		return filename;
	}
}