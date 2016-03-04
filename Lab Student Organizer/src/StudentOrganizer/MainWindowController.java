package StudentOrganizer;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

//Controller file for MainWindow.fxml. This handles all of the different actions, different properties of the components,
//as well as the loading/saving serialization. Implementing Initializable allows the method initialize to be called
//when the window is created
public class MainWindowController implements Initializable {

    //main components that will be used throughout. These are connected to the fxml components with matching fx:id's
    public TableView DataTable;
    public TableColumn KeyCol, ValCol;
    public ComboBox<String> cbName;
    public Button btnEdit;

    //A list of all the students loaded from the save file or added manually
    private ObservableList<Student> students = FXCollections.observableArrayList();
    //A Set of all the names of the students, used for the comboBox items
    private Set<String> studentNames = new TreeSet<>();

    private int selectedStudentIndex = -1;

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

        //students.add(new Student("123456", "name:Johnny", "grade:A", "comment:winning", "student id:561235", "level of awesome:530"));

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

        //set the items on the combobox to be all of the different available students
        if(students.size() != 0)
            students.forEach(e -> studentNames.add(e.getName()));
        cbName.setItems(FXCollections.observableArrayList(studentNames));
    }

    //finds the student with the name typed into the combobox
    public void lookupStudent() {
        String name = cbName.getEditor().getText();

        if (name.equals(""))
            return;

        //shifts focus to the data table, and sets window title to show who's being looked at
        DataTable.requestFocus();
        Main.title.setValue("Student Lookup - " + name);

        for (int i = 0; i < students.size(); i++)
            //worth noting that this is an overriden equals method, not comparing memory addresses
            if (students.get(i).equals(name)) {
                DataTable.setItems(FXCollections.observableArrayList(students.get(i).getData()));
                cbName.setPromptText("Student Name");
                btnEdit.setDisable(false);
                selectedStudentIndex = i;
                return;
            }

        //cbName.setValue("Student Not Found!");
        DataTable.setItems(null);
        btnEdit.setDisable(true);
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
        System.out.println("load: " + students);
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

    //Creates the popout, retrieves the final product, and adds it to the combobox items as well as the overall student list
    public void addStudent() {
        PopoutController pc = new PopoutController();
        pc.show();

        Student tempStud;
        if ((tempStud = pc.getFinalStudent()) != null) {
            String tempName = tempStud.getName();
            if (studentNames.contains(tempName)) {
                int copyNumber = 1;
                while (studentNames.contains(tempName + " (" + copyNumber + ")"))
                    copyNumber++;
                tempName = tempName + " (" + copyNumber + ")";
            }
            studentNames.add(tempName);
            tempStud.setName(tempName);
            students.add(tempStud);
            cbName.setItems(FXCollections.observableArrayList(studentNames));
        }
    }

    public void editStudent() {
        System.out.println("hit edit with " + students.get(selectedStudentIndex));
        PopoutController pc = new PopoutController(students.get(selectedStudentIndex));
        pc.show();

        students.set(selectedStudentIndex, pc.getFinalStudent());
    }
}
