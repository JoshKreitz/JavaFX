package sample;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MovieTab extends Tab {

    @FXML
    public TableView<Movie> mainTable = new TableView<>();
    public ObservableList<Movie> movies = FXCollections.observableArrayList();

    @FXML
    public TableColumn TitleCol, GenreCol, RatingCol, LengthCol, DirectorCol, StarringActorCol;

    public MovieTab(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MovieTab.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

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
    }

    public MovieTab(String title){
        this();
        setText(title);
    }

    public TableView<Movie> getMainTable(){
        return mainTable;
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
        Controller.popout.setOnCloseRequest(e -> closePopout(false));

        System.out.println("1");

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
