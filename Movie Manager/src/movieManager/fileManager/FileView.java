package movieManager.fileManager;

/**
 * A basic representation of a file that can be displayed in the Table
 */
public class FileView {
	// the filename
	private String name = "";
	// the type, either "Dir", "File", or "Unk"
	private String type = "";
	// if a directory, this will contain a basic list of file contents
	private String contents = "";

	public FileView() {
	}

	public FileView(String name, String type, String contents) {
		this.name = name;
		this.type = type;
		this.contents = contents;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
}
