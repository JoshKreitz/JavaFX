package sample;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static Stage popout;

    public TableView<Movie> mainTable = new TableView<Movie>();
    public static ObservableList<Movie> movies = FXCollections.observableArrayList();

    public Button editButton;
    public Button deleteButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        movies.add(new Movie("TestMovie1", "Genre", "Rating", "Length", "Director", "StarringActor", 8));
        movies.add(new Movie("TestMovie2", "Genre", "Rating", "Length", "Director", "StarringActor", 8));
        movies.add(new Movie("TestMovie3", "Genre", "Rating", "Length", "Director", "StarringActor", 8));
        movies.add(new Movie("TestMovie4", "Genre", "Rating", "Length", "Director", "StarringActor", 8));
        mainTable.setItems(movies);
        mainTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        mainTable.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>()
        {
            private int size;
            @Override
            public void onChanged(Change<? extends Integer> change)
            {
                if((size = change.getList().size()) >= 1){
                    deleteButton.setDisable(false);
                    if(size == 1)
                        editButton.setDisable(false);
                    else editButton.setDisable(true);
                }
                else {
                    deleteButton.setDisable(true);
                    editButton.setDisable(true);
                }
            }

        });
    }

    public void showMoviePopout(){
        PopoutController.labelText = "Add A Movie";
        PopoutController.editing = false;
        PopoutController.createButtonText = "Create";

        popout = new Stage();
        popout.initModality(Modality.APPLICATION_MODAL);
        popout.setTitle("Add Movie");

        try {
            Parent root = FXMLLoader.load(getClass().getResource("popout.fxml"));
            popout.setScene(new Scene(root, 250,330));
            popout.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closePopout(){
        popout.hide();
    }

    private static int indexOfEdittingRow = -1;

    public static void addMovie(Movie mv){
        if(indexOfEdittingRow != -1) {
            movies.add(indexOfEdittingRow, mv);
            indexOfEdittingRow = -1;
        }
        else movies.add(mv);
        closePopout();
    }

    public void deleteSelectedItems(){
        mainTable.getItems().removeAll(mainTable.getSelectionModel().getSelectedItems());
        mainTable.getSelectionModel().clearSelection();
    }

    public void editSelectedItem(){
        Movie toBeEditted = mainTable.getSelectionModel().getSelectedItem();
        indexOfEdittingRow = mainTable.getSelectionModel().getSelectedIndex();

        PopoutController.editing = true;
        PopoutController.labelText = "Edit Movie";
        PopoutController.titleText = toBeEditted.getTitle();
        PopoutController.genreText = toBeEditted.getGenre();
        PopoutController.ratingText = toBeEditted.getRating();
        PopoutController.lengthText = toBeEditted.getLength();
        PopoutController.directorText = toBeEditted.getDirector();
        PopoutController.starringActorText = toBeEditted.getStarringActor();
        PopoutController.scoreNum = toBeEditted.getScoreOutOfTen();
        PopoutController.createButtonText = "Edit";


        popout = new Stage();
        popout.initModality(Modality.APPLICATION_MODAL);
        popout.setTitle("Add Movie");

        try {
            Parent root = FXMLLoader.load(getClass().getResource("popout.fxml"));
            popout.setScene(new Scene(root, 250,330));
            popout.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        movies.remove(toBeEditted);
    }
}






















