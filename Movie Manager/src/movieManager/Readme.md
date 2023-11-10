

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