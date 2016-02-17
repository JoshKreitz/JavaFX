package sample.BackupBeforeTabs;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {

    private static Stage popout;

    public TableView<Movie> mainTable = new TableView<>();
    public static ObservableList<Movie> movies = FXCollections.observableArrayList();

    public Button editButton, deleteButton;
    public TableColumn TitleCol, GenreCol, RatingCol, LengthCol, DirectorCol, StarringActorCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Scanner in;
        try {
            in = new Scanner(new File(System.getProperty("user.home") + "\\Movie List.txt"));
            String[] line;
            while (in.hasNextLine()) {
                line = in.nextLine().split(",");
                movies.add(new Movie(line[0].replaceAll("~", ""), line[1].replaceAll("~", ""), line[2].replaceAll("~", ""), line[3].replaceAll("~", ""), line[4].replaceAll("~", ""), line[5].replaceAll("~", ""), Double.parseDouble(line[6].replaceAll("~", ""))));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        mainTable.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
            private int size;

            @Override
            public void onChanged(Change<? extends Integer> change) {
                if ((size = change.getList().size()) >= 1) {
                    deleteButton.setDisable(false);
                    if (size == 1)
                        editButton.setDisable(false);
                    else editButton.setDisable(true);
                } else {
                    deleteButton.setDisable(true);
                    editButton.setDisable(true);
                }
            }

        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    BufferedWriter out = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "\\Movie List.txt"));
                    for (Movie mv : movies) {
                        out.write(mv.getTitle() + "~," + mv.getGenre() + "~," + mv.getRating() + "~," + mv.getLength() + "~," + mv.getDirector() + "~," + mv.getStarringActor() + "~," + mv.getScoreOutOfTen() + "~");
                        out.newLine();
                    }
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showMoviePopout() {
        PopoutController.labelText = "Add A Movie";
        PopoutController.createButtonText = "Create";

        openPopout("Add Movie");
    }

    private static int indexOfEdittingRow = -1;
    private static boolean hangingDelete = true;

    public static void closePopout(boolean addedElement) {
        if (!addedElement && indexOfEdittingRow != -1)
            hangingDelete = false;
        popout.hide();
    }

    public static void addMovie(Movie mv) {
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

        openPopout("Edit Movie");

        if (hangingDelete)
            movies.remove(toBeEditted);
        else hangingDelete = true;
    }

    private void openPopout(String title) {
        popout = new Stage();
        popout.initModality(Modality.APPLICATION_MODAL);
        popout.setTitle(title);
        popout.setOnCloseRequest(e -> closePopout(false));

        try {
            Parent root = FXMLLoader.load(getClass().getResource("popout.fxml"));
            popout.setScene(new Scene(root, 250, 340));
            popout.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}













/*
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Menu?>
<?import javafx.scene.control.MenuBar?>
<?import javafx.scene.control.MenuItem?>
<?import javafx.scene.control.TableColumn?>
<?import javafx.scene.control.TableView?>
<?import javafx.scene.control.cell.PropertyValueFactory?>
<?import javafx.scene.layout.BorderPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.Region?>
<?import javafx.scene.layout.VBox?>

<BorderPane maxHeight="-Infinity" maxWidth="-Infinity" minHeight="-Infinity" minWidth="-Infinity" prefHeight="400.0" prefWidth="600.0" xmlns="http://javafx.com/javafx/null" xmlns:fx="http://javafx.com/fxml/1" fx:controller="sample.Controller">
   <top>
      <VBox prefWidth="100.0" BorderPane.alignment="CENTER">
         <children>
            <MenuBar>
              <menus>
                <Menu mnemonicParsing="false" text="File">
                  <items>
                    <MenuItem mnemonicParsing="false" text="Close" />
                  </items>
                </Menu>
                <Menu mnemonicParsing="false" text="Edit">
                  <items>
                    <MenuItem mnemonicParsing="false" text="Delete" />
                  </items>
                </Menu>
                <Menu mnemonicParsing="false" text="Help">
                  <items>
                    <MenuItem mnemonicParsing="false" text="About" />
                  </items>
                </Menu>
              </menus>
            </MenuBar>
            <HBox prefWidth="200.0" spacing="10.0">
               <children>
                  <Button mnemonicParsing="false" onAction="#showMoviePopout" text="Add Movie" />
                  <Region minWidth="-Infinity" prefWidth="-Infinity" HBox.hgrow="ALWAYS" />
                  <Button fx:id="editButton" disable="true" mnemonicParsing="false" text="Edit" onAction="#editSelectedItem"/>
                  <Button fx:id="deleteButton" disable="true" mnemonicParsing="false" text="Remove" onAction="#deleteSelectedItems"/>
               </children>
               <VBox.margin>
                  <Insets bottom="8.0" left="8.0" right="8.0" top="8.0" />
               </VBox.margin>
            </HBox>
         </children>
         <opaqueInsets>
            <Insets right="8.0" top="8.0" />
         </opaqueInsets>
         <BorderPane.margin>
            <Insets />
         </BorderPane.margin>
      </VBox>
   </top>
   <center>
      <TableView fx:id="mainTable" editable="true" prefHeight="200.0" prefWidth="200.0" BorderPane.alignment="CENTER">
        <columns>
          <TableColumn fx:id="TitleCol" maxWidth="500.0" minWidth="200.0" prefWidth="200.0" text="Title">
              <cellValueFactory>
                  <PropertyValueFactory property="title" />
              </cellValueFactory>
          </TableColumn>
            <TableColumn fx:id="GenreCol" maxWidth="300.0" minWidth="150.0" prefWidth="150.0" text="Genre" cellFactory="${TitleCol.cellFactory}">
                <cellValueFactory>
                    <PropertyValueFactory property="genre" />
                </cellValueFactory>
            </TableColumn>
            <TableColumn fx:id="RatingCol" maxWidth="300.0" minWidth="75.0" prefWidth="75.0" text="Rating" cellFactory="${TitleCol.cellFactory}">
                <cellValueFactory>
                    <PropertyValueFactory property="rating" />
                </cellValueFactory>
            </TableColumn>
            <TableColumn fx:id="LengthCol" maxWidth="300.0" minWidth="75.0" prefWidth="75.0" text="Length" cellFactory="${TitleCol.cellFactory}">
                <cellValueFactory>
                    <PropertyValueFactory property="length" />
                </cellValueFactory>
            </TableColumn>
            <TableColumn fx:id="DirectorCol" maxWidth="300.0" minWidth="75.0" prefWidth="75.0" text="Director" cellFactory="${TitleCol.cellFactory}">
                <cellValueFactory>
                    <PropertyValueFactory property="director" />
                </cellValueFactory>
            </TableColumn>
            <TableColumn fx:id="StarringActorCol" maxWidth="300.0" minWidth="100.0" prefWidth="100.0" text="Starring Actor" cellFactory="${TitleCol.cellFactory}">
                <cellValueFactory>
                    <PropertyValueFactory property="starringActor" />
                </cellValueFactory>
            </TableColumn>
            <TableColumn maxWidth="300.0" minWidth="75.0" prefWidth="75.0" text="Rating /10">
                <cellValueFactory>
                    <PropertyValueFactory property="scoreOutOfTen" />
                </cellValueFactory>
            </TableColumn>
        </columns>
      </TableView>
   </center>
</BorderPane>

 */








