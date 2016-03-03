package StudentOrganizer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    public TableView DataTable;
    public TableColumn KeyCol, ValCol;
    public TextField tfName;

    public ObservableList<Student> students = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSaveFile(System.getProperty("user.home") + "/Desktop/testing.ser");

        students.add(new Student("Johnny", "grade:A", "comment:winning"));

        KeyCol.prefWidthProperty().bind(DataTable.widthProperty().divide(2));
        ValCol.prefWidthProperty().bind(DataTable.widthProperty().divide(2));


        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                saveFile(System.getProperty("user.home") + "/Desktop/testing.ser");
            }
        });

    }

    public void loadStudent(){
        String name = tfName.getText();
        for (Student stud : students)
            if (stud.equals(name)) {
                DataTable.setItems(FXCollections.observableArrayList(stud.getData()));
                tfName.setPromptText("Student Name");
                return;
            }
        tfName.setText("");
        tfName.setPromptText("Student Not Found!");
        DataTable.setItems(null);
    }

    private void loadSaveFile(String path) {
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            students.addAll(((SerializableList) in.readObject()).getStudents());
            in.close();
            fileIn.close();
        } catch (IOException e) {
            System.out.println("load file IOException");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("load file ClassNotFoundException");
        }
        //System.out.println("load: " + students);
    }

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
