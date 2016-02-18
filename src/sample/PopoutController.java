package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class PopoutController implements Initializable {

    public static String labelText = "", titleText = "", genreText = "", lengthText = "", ratingText = "", directorText = "", starringActorText = "", createButtonText = "";
    public static double scoreNum = 0;

    public static boolean addMovie;
    public static Movie movieToAdd;

    public Label lbl;
    public TextField tfTitle, tfLength, tfDirector, tfStarringActor;
    public ComboBox<String> cbGenre, cbRating;
    public Slider sScore;
    public Button CancelButton, CreateButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lbl.setText(labelText);
        tfTitle.setText(titleText);
        cbGenre.setValue(genreText);
        if(!ratingText.equals(""))
            cbRating.setValue(ratingText);
        tfLength.setText(lengthText);
        tfDirector.setText(directorText);
        tfStarringActor.setText(starringActorText);
        sScore.setValue(scoreNum);

        CreateButton.setText(createButtonText);

        labelText = titleText = genreText = lengthText = ratingText = directorText = starringActorText = "";
        scoreNum = 0;
    }

    //TODO stop this from making multiple listeners if failed via textbox
    public void addMovie(){
        if(tfTitle.getText().trim().equals("")){
            tfTitle.setStyle("-fx-background-color: red");
            tfTitle.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue) {
                    tfTitle.setStyle(null);
                    tfTitle.setText("");
                }
            });
        }
        else{
            String temp;
            if((temp = cbRating.getValue()) == null)
                temp = "";
            addMovie = true;
            movieToAdd = new Movie(tfTitle.getText(), cbGenre.getValue(), temp, tfLength.getText(), tfDirector.getText(), tfStarringActor.getText(), sScore.getValue());
            MovieTab.closePopout(true);
        }

    }

    public void closePopout(){
        addMovie = false;
        MovieTab.closePopout(false);
    }
}
