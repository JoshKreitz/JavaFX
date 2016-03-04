package StudentOrganizer;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

//This class entirely handles the popout for both adding and (eventually) editting students.
public class PopoutController implements Initializable {

    //the popout and its different components
    private static Stage popout;
    public ScrollPane scrollPane;
    public VBox spContent;
    public TextField tfID;

    //final product. Only not null when the user clicks confirm
    private static Student finalStudent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //sets up the scroll pane and it's content layout
        spContent = new VBox();
        scrollPane.setContent(spContent);
        scrollPane.setStyle("-fx-background-color:transparent;");
        spContent.setSpacing(8);
        spContent.setAlignment(Pos.TOP_CENTER);

        //starts off with three tag areas, more can be added
        spContent.getChildren().addAll(new KeyValBox(), new KeyValBox(), new KeyValBox());
    }

    //called when the user is done editting/adding and wishes to save the student. First retrieves the tags from the boxes, then instantiates finalStudent
    public void confirm() {
        ArrayList<String> tags = new ArrayList<>();
        String key;
        for (Node tag : spContent.getChildren())
            if (!(key = ((KeyValBox) tag).getKey()).equals(""))
                tags.add(key + ":" + (((KeyValBox) tag).getVal()) + "~~__123");

        String[] stringTags = new String[tags.size()];
        for (int i = 0; i < stringTags.length; i++)
            stringTags[i] = tags.get(i);

        finalStudent = new Student(tfID.getText(), stringTags);
        popout.close();
    }

    //called to close the popout
    public void cancel() {
        finalStudent = null;
        popout.close();
    }

    //returns the final product
    public Student getFinalStudent() {
        return finalStudent;
    }

    //adds a new row for editing, see KeyValBox description below
    public void addTag() {
        spContent.getChildren().add(new KeyValBox());
    }

    //creates the popout window and shows it
    public void show() {
        popout = new Stage();

        popout.setTitle("Edit/Add Student");
        popout.setMinHeight(250);

        //stops the main window from being interacted with until this one closes
        popout.initModality(Modality.APPLICATION_MODAL);

        try {
            Parent root = FXMLLoader.load(getClass().getResource("Popout.fxml"));
            popout.setScene(new Scene(root, 400, 500));

            //hijacks main thread, completely stopping code in main window. This means that once this popout closes, the thread will return to where show() was called,
            //in this case addStudent() method in MainWindowController.java.
            popout.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //custom layout for the two text boxes seperated by the colon. This saves having to individually create a bunch of these over and over, packaging everything
    //into one neat place
    public class KeyValBox extends HBox {

        private TextField tfKey, tfVal;
        private Label colonLbl;

        public KeyValBox() {
            super();

            tfKey = new TextField();
            tfKey.prefWidthProperty().bind(scrollPane.widthProperty().subtract(17).divide(2));
            tfVal = new TextField();
            tfVal.prefWidthProperty().bind(scrollPane.widthProperty().subtract(17).divide(2));
            colonLbl = new Label(":");
            colonLbl.setFont(Font.font(colonLbl.getFont().getName(), 15));

            getChildren().addAll(tfKey, colonLbl, tfVal);
            setSpacing(5);
        }

        public String getKey() {
            return tfKey.getText();
        }

        public void setKey(String key) {
            tfKey.setText(key);
        }

        public String getVal() {
            return tfVal.getText();
        }

        public void setVal(String val) {
            tfVal.setText(val);
        }
    }
}






















