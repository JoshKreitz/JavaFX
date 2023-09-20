package movieManager.fileManager;

public class FileView {
	
	private String name = "";
	private String type = "";
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
