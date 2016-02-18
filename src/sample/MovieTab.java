package sample;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

//Draggable Tabs? http://stackoverflow.com/questions/16437615/javafx-tab-positioning-on-mouse-drag-drop
public class MovieTab extends Tab {

    public TableView<Movie> mainTable = new TableView<>();
    public ObservableList<Movie> movies = FXCollections.observableArrayList();

    public TableColumn TitleCol, GenreCol, RatingCol, LengthCol, DirectorCol, StarringActorCol;

    public Label titleLabel = new Label();
    public String tabTitle = "New Tab";
    public TextField titleTF = new TextField();

    public MovieTab(String title){
        tabTitle = title;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MovieTab.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        titleLabel = new Label(title);
        setGraphic(titleLabel);
        titleLabel.setOnMouseClicked(e -> {
            if(e.getClickCount() == 2) {
                titleTF.setText(tabTitle);
                setGraphic(titleTF);
                titleTF.selectAll();
                titleTF.requestFocus();
            }
        });
        titleTF.setOnAction(e -> {
            if(!titleTF.getText().trim().equals("")) {
                tabTitle = titleTF.getText();
                titleLabel.setText(tabTitle);
            }
            setGraphic(titleLabel);
        });
        titleTF.focusedProperty().addListener((arg0, oldVal, newVal) -> {
                if(!newVal){
                    if(!titleTF.getText().trim().equals("")) {
                        tabTitle = titleTF.getText();
                        titleLabel.setText(tabTitle);
                    }
                    setGraphic(titleLabel);
                }
        });

        EventHandler<TableColumn.CellEditEvent> handler = e -> movies.get(e.getTablePosition().getRow()).changeProptery(((PropertyValueFactory) e.getTableColumn().getCellValueFactory()).getProperty(), (String) e.getNewValue());

        TitleCol.setCellFactory(TextFieldTableCell.<Movie>forTableColumn());
        TitleCol.setOnEditCommit(handler);
        GenreCol.setOnEditCommit(handler);
        RatingCol.setOnEditCommit(handler);
        LengthCol.setOnEditCommit(handler);
        DirectorCol.setOnEditCommit(handler);
        StarringActorCol.setOnEditCommit(handler);

        mainTable.setItems(movies);
        mainTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        mainTable.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
            private int size;

            @Override
            public void onChanged(Change<? extends Integer> change) {
                if ((size = change.getList().size()) >= 1) {
                    Controller.deleteEnabled.setValue(false);
                    if (size == 1)
                        Controller.editEnabled.setValue(false);
                    else Controller.editEnabled.setValue(true);
                } else {
                    Controller.editEnabled.setValue(true);
                    Controller.deleteEnabled.setValue(true);
                }
            }
        });
    }

    public MovieTab(){
        this("New Tab");
    }


    private static int indexOfEdittingRow = -1;
    private static boolean hangingDelete = true;

    public static void closePopout(boolean addedElement) {
        if (!addedElement && indexOfEdittingRow != -1)
            hangingDelete = false;
        Controller.popout.hide();
    }

    public void addMovie(Movie mv) {
        if (indexOfEdittingRow != -1) {
            movies.add(indexOfEdittingRow, mv);
            indexOfEdittingRow = -1;
        } else movies.add(mv);
        closePopout(true);
    }

    public void deleteSelectedItems() {
        mainTable.getItems().removeAll(mainTable.getSelectionModel().getSelectedItems());
        mainTable.getSelectionModel().clearSelection();
    }

    public void editSelectedItem() {
        Movie toBeEditted = mainTable.getSelectionModel().getSelectedItem();
        indexOfEdittingRow = mainTable.getSelectionModel().getSelectedIndex();

        PopoutController.labelText = "Edit Movie";
        PopoutController.titleText = toBeEditted.getTitle();
        PopoutController.genreText = toBeEditted.getGenre();
        PopoutController.ratingText = toBeEditted.getRating();
        PopoutController.lengthText = toBeEditted.getLength();
        PopoutController.directorText = toBeEditted.getDirector();
        PopoutController.starringActorText = toBeEditted.getStarringActor();
        PopoutController.scoreNum = toBeEditted.getScoreOutOfTen();
        PopoutController.createButtonText = "Edit";

        Controller.popout = new Stage();
        Controller.popout.initModality(Modality.APPLICATION_MODAL);
        Controller.popout.setTitle("Add Movie");
        Controller.popout.setResizable(false);
        Controller.popout.setOnCloseRequest(e -> closePopout(false));

        try {
            Parent root = FXMLLoader.load(getClass().getResource("popout.fxml"));
            Controller.popout.setScene(new Scene(root, 250, 340));
            Controller.popout.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(PopoutController.addMovie)
            addMovie(PopoutController.movieToAdd);

        if (hangingDelete)
            movies.remove(toBeEditted);
        else hangingDelete = true;
    }

    public TableView<Movie> getMainTable(){
        return mainTable;
    }

    public String getTabTitle(){
        return tabTitle;
    }

    public ObservableList<Movie> getMovies(){
        return movies;
    }

    public void loadMovie(Movie mv){
        movies.add(mv);
    }

    public void setMainTable(TableView<Movie> mainTable) {
        this.mainTable = mainTable;
    }

    public TableColumn getTitleCol() {
        return TitleCol;
    }

    public void setTitleCol(TableColumn titleCol) {
        TitleCol = titleCol;
    }

    public TableColumn getGenreCol() {
        return GenreCol;
    }

    public void setGenreCol(TableColumn genreCol) {
        GenreCol = genreCol;
    }

    public TableColumn getRatingCol() {
        return RatingCol;
    }

    public void setRatingCol(TableColumn ratingCol) {
        RatingCol = ratingCol;
    }

    public TableColumn getLengthCol() {
        return LengthCol;
    }

    public void setLengthCol(TableColumn lengthCol) {
        LengthCol = lengthCol;
    }

    public TableColumn getDirectorCol() {
        return DirectorCol;
    }

    public void setDirectorCol(TableColumn directorCol) {
        DirectorCol = directorCol;
    }

    public TableColumn getStarringActorCol() {
        return StarringActorCol;
    }

    public void setStarringActorCol(TableColumn starringActorCol) {
        StarringActorCol = starringActorCol;
    }
}
