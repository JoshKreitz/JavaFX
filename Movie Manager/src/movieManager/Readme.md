

File Structure:
movieManager
        ├── config - manage the core configuration file
        │   ├── ConfigController.java - controller to display the form and the error console
        │   └── Config.fxml
        │   └── ConfigFile.java - file manager to handle saving/loading
        ├── fileManager - 
        │   ├── FileManagerController.java
        │   ├── FileManager.fxml
        │   └── FileView.java
        ├── Main.java
        ├── metadata
        │   ├── MetadataManager.java - handle the metadata map, loading/saving/pruning/updating
        │   ├── MovieMetadata.java - contains all the serializeable metadata for a specific movie
        │   ├── NetworkHandler.java - handle all outgoing network requests
        │   ├── SearchMovie.java - bean for parsing movie JSON
        │   ├── SearchResults.java - bean for parsing search results JSON
        │   └── Serializer.java - handle saving/loading the metadata map
        ├── movieShelf
        │   ├── MoviePane.fxml
        │   ├── MoviePane.java - UI element for displaying a single movie based on it's metadata
        │   ├── ShelfController.java
        │   └── Shelf.fxml
        
        
Metadata flow:
- Metadata manager:
	1 try to serialize from filesystem
	2 read filesystem, detect differences
	3 create default entries for new files
	4 send requests to fetch data from the API
	5 update metadata entries with API data
	6 (future) download image
	7 on closing the application, save the data