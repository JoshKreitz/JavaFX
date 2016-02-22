package sample;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {

    public static Stage popout;

    //TODO Java 9: draggable tabs
    public TabPane tabPane;
    public ObservableList<MovieTab> tabs = FXCollections.observableArrayList();

    public Button editButton, deleteButton;
    public static BooleanProperty editEnabled, deleteEnabled;

    public Label pathLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Scanner in;
        try {
            in = new Scanner(new File(System.getProperty("user.home") + "/Movie List.txt"));
            in.nextLine();
            in.nextLine();
            String line;
            String[] lineArr;
            MovieTab newTab = null;
            while (in.hasNextLine()){
                if ((line = in.nextLine()).equals("##### NEW TAB #####")) {
                    if (newTab != null)
                        tabs.add(newTab);
                    newTab = new MovieTab(in.nextLine());
                } else {
                    lineArr = line.split(",");
                    try {
                        newTab.loadMovie(new Movie(lineArr[0].replaceAll("~", ""), lineArr[1].replaceAll("~", ""), lineArr[2].replaceAll("~", ""), lineArr[3].replaceAll("~", ""), lineArr[4].replaceAll("~", ""), lineArr[5].replaceAll("~", ""), Double.parseDouble(lineArr[6].replaceAll("~", ""))));
                    } catch(Exception e){
                        System.out.println("Failed on line: \"" + line + "\"");
                    }
                }
            }
            tabs.add(newTab);
        } catch (Exception e) {
            System.out.println("cant find file or it's screwed up");
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                saveFile();
            }
        });

        pathLabel.setText("Editable Save File Located At " + System.getProperty("user.home") + "/Movie List.txt");

        editEnabled = new SimpleBooleanProperty(true);
        deleteEnabled = new SimpleBooleanProperty(true);

        editButton.disableProperty().bind(editEnabled);
        deleteButton.disableProperty().bind(deleteEnabled);

        if(tabs.size() == 0)
            tabs.add(new MovieTab("New Tab"));
        tabPane.getTabs().setAll(tabs);
    }

    public void showMoviePopout() {
        PopoutController.labelText = "Add A Movie";
        PopoutController.createButtonText = "Create";

        popout = new Stage();
        popout.initModality(Modality.APPLICATION_MODAL);
        popout.setTitle("Add Movie");
        popout.setResizable(false);
        popout.setOnCloseRequest(e -> MovieTab.closePopout(false));

        try {
            Parent root = FXMLLoader.load(getClass().getResource("popout.fxml"));
            popout.setScene(new Scene(root, 250, 340));
            popout.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(PopoutController.addMovie)
            ((MovieTab)tabPane.getSelectionModel().getSelectedItem()).addMovie(PopoutController.movieToAdd);
    }

    public void editSelectedItem(){
        ((MovieTab)tabPane.getSelectionModel().getSelectedItem()).editSelectedItem();
    }

    public void deleteSelectedItems(){
        ((MovieTab)tabPane.getSelectionModel().getSelectedItem()).deleteSelectedItems();
    }

    public void addTab(){
        MovieTab movieTab = new MovieTab("New Tab");

        tabs.add(movieTab);
        tabPane.getTabs().setAll(tabs);
    }

    public BooleanProperty editEnabledProperty(){
        return editEnabled;
    }

    public BooleanProperty deleteEnabledProperty(){
        return deleteEnabled;
    }

    public void saveFile(){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "/Movie List.txt"));
            out.write("This text file contains the movie listings from Movie List. Editing will not cause problems, as long as the syntax remains constant.");
            out.newLine();
            out.write("Note: For every empty value, there must be a \"~,\" as a placeholder!");
            out.newLine();
            for (Tab tab : tabPane.getTabs()) {
                out.write("##### NEW TAB #####");
                out.newLine();
                out.write(((MovieTab)tab).getTabTitle());
                out.newLine();
                for(Movie mv: ((MovieTab)tab).getMovies()) {
                    out.write(mv.getTitle() + "~," + mv.getGenre() + "~," + mv.getRating() + "~," + mv.getLength() + "~," + mv.getDirector() + "~," + mv.getStarringActor() + "~," + mv.getScoreOutOfTen() + "~");
                    out.newLine();
                }
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Stage moviePopout;
    private Label randMovieLabel;

    public void randomMovie(){
        moviePopout = new Stage();
        moviePopout.initModality(Modality.APPLICATION_MODAL);
        moviePopout.setTitle("Random Movie");
        moviePopout.setResizable(false);

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(8, 8, 8, 8));
        layout.setSpacing(10);

        randMovieLabel = new Label();
        randMovieLabel.setText("Go Watch " + getRandMovie());
        layout.getChildren().add(randMovieLabel);

        HBox forButtons = new HBox();
        forButtons.setSpacing(10);
        forButtons.setAlignment(Pos.CENTER);
        Button repick = new Button("That's dumb, pick another!"), okay = new Button("Okay, I'll watch that one!");
        repick.setOnAction(e -> randMovieLabel.setText("Go Watch " + getRandMovie()));
        okay.setOnAction(e -> moviePopout.hide());
        forButtons.getChildren().add(repick);
        forButtons.getChildren().add(okay);
        layout.getChildren().add(forButtons);

        Scene scene = new Scene(layout, 330, 75);

        moviePopout.setScene(scene);
        moviePopout.showAndWait();
    }

    private String getRandMovie(){
        MovieTab randTab = tabs.get((int)(Math.random() * tabs.size()));
        ObservableList<Movie> movies = randTab.getMovies();
        return movies.get((int)(Math.random() * movies.size())).getTitle() + " in tab \"" + randTab.getTabTitle() + "\"";
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








