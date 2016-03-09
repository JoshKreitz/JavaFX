package T2Final;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by User on 3/9/2016.
 */
public class MainWindowController implements Initializable{

    private Thesaurus thes = new Thesaurus();

    public TextField mainTextField;
    public ScrollPane scrollPane;
    public VBox scrollPaneLayout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Scanner in = new Scanner(new File("T2FinalExam/src/T2Final/Words.txt"));
            while(in.hasNextLine())
                thes.load(in.nextLine());
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void lookUpWord(){
        System.out.println("hit");

        scrollPaneLayout = new VBox();
        scrollPane.setContent(scrollPaneLayout);

        System.out.println("text " + mainTextField.getText());
        Set<String> synonyms = thes.getSyns(mainTextField.getText());
        for(String syn: synonyms)
            scrollPaneLayout.getChildren().add(new Label(syn));
    }
}
