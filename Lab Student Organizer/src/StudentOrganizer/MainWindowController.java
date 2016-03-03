package StudentOrganizer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

//Controller file for MainWindow.fxml. This handles all of the different actions, different properties of the components,
//as well as the loading/saving serialization. Implementing Initializable allows the method initialize to be called
//when the window is created
public class MainWindowController implements Initializable {

    //main components that will be used throughout. These are connected to the fxml components with matching fx:id's
    public TableView DataTable;
    public TableColumn KeyCol, ValCol;
    public ComboBox<String> cbName;

    //A list of all the students loaded from the save file
    public ObservableList<Student> students = FXCollections.observableArrayList();

    //called only once, when window is created
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //populates students array with students loaded from save file
        loadSaveFile(System.getProperty("user.home") + "/Desktop/testing.ser");
        //adds code to save the students before the program exits
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                saveFile(System.getProperty("user.home") + "/Desktop/testing.ser");
            }
        });

        students.add(new Student("123456", "name:Johnny", "grade:A", "comment:winning", "student id:561235", "level of awesome:530"));

        //Sets the width of the columns to be half of the TableView's width
        KeyCol.prefWidthProperty().bind(DataTable.widthProperty().subtract(2).divide(2));
        ValCol.prefWidthProperty().bind(DataTable.widthProperty().subtract(2).divide(2));

        //removes the titles from the two columns, they are unneeded for this program
        DataTable.widthProperty().addListener((source, oldWidth, newWidth) -> {
            Pane header = (Pane) DataTable.lookup("TableHeaderRow");
            if (header.isVisible()) {
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        //kind of like a Swing FocusListener, if cbName gains focus it will clear the field
        cbName.focusedProperty().addListener((arg0, oldVal, newVal) -> {
            if (newVal)
                cbName.setValue("");
        });
    }

    //finds the student with the name typed into the combobox
    public void lookupStudent() {
        String name = cbName.getValue();
        if (name.equals(""))
            return;

        for (Student stud : students)
            //worth noting that this is an overriden equals method, not comparing memory addresses
            if (stud.equals(name)) {
                DataTable.setItems(FXCollections.observableArrayList(stud.getData()));
                cbName.setPromptText("Student Name");
                return;
            }

        cbName.setValue("Student Not Found!");
        DataTable.setItems(null);
    }

    //loads the file via Serialization. Too complex for me to explain, look it up, it's awesome. Pretty much saving objects directly
    private void loadSaveFile(String path) {
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            students.addAll(((SerializableList) in.readObject()).getStudents());
            in.close();
            fileIn.close();
        } catch (IOException e) {
            System.out.println("load file IOException");
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("load file ClassNotFoundException");
        }
        //System.out.println("load: " + students);
    }

    //saves the students
    private void saveFile(String path) {
        //System.out.println("save: " + students);
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(new SerializableList(students.toArray()));
            out.close();
            fileOut.close();
        } catch (IOException e) {
        }
    }
}
