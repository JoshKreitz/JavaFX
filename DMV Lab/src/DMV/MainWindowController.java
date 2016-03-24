package DMV;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

//Controller file for MainWindow.fxml. This handles all of the different actions and different properties of the components.
//Implementing Initializable allows the method initialize to be called when the window is created
public class MainWindowController implements Initializable {

    //THESE VALUES MADE TO BE EDITED

    private final int waitTimeAtSignIn = 10;
    private final int waitTimeAtLicense = 90;
    private final int waitTimeAtRegistration = 60;
    private final int waitTimeAtCashierToPay = 30;
    private final int waitTimeAtCashierWithCheck = 10;
    private final int waitTimeAtCheck = 5;

    //END EDITING


    //main cavas, where all the graphics are drawn/painted
    public Canvas canvas;

    // the various labels accross the top
    public Label lblEntryTime, lblExitTime, lblElapsedTime;

    //where the user will enter the string representing a person
    //example: 10 R C Bob   will create a person that arrives at 10:00 for car registration, pays via check, with name Bob
    public TextField tfEntryField;

    //this class handles all of the graphics
    private GraphicsProcessor gp;

    //the unit of time, this can be altered to speed up or slow down time
    private int waitTime = 1000;

    //called only once, when window is created
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //uncomment this to get canvas coordinates on click, very useful
        //canvas.setOnMouseClicked(e -> System.out.println(e.getX() + ", " + e.getY()));

        //starts off the program with a blank slate when it opens
        gp = new GraphicsProcessor(canvas.getGraphicsContext2D(), "");
    }

    //sets the time to one second cooldown, "normal time"
    public void oneXTime() {
        waitTime = 1000;
    }

    //speeds up the perspective time by a factor of 2
    public void twoXTime() {
        waitTime = 500;
    }

    //speeds up the perspective time by a factor of 10
    public void tenXTime() {
        waitTime = 100;
    }

    //these variables are different errors that the user could run into when entering a new person
    private final int NotEnoughParams = 1, NumberFormatException = 2, ImproperRoute = 3, ImproperPayment = 4, NameTooLong = 5;

    //called when the user wants to begin the waiting
    public void start() {
        //retrieves the graphics from the canvas, this is what's actually painted on
        GraphicsContext g = canvas.getGraphicsContext2D();

        //reset labels
        lblEntryTime.setText("Entry Time: 00:00");
        lblExitTime.setText("Exit Time: 00:00");
        lblElapsedTime.setText("Elapsed Time: 00");

        //retrieve the text from the textbox, makes sure there are enough parameters. If not, display error message
        String[] person = tfEntryField.getText().split(" ");
        if (person.length < 4) {
            gp.writeError(g, NotEnoughParams);
            return;
        }

        //retrieves the arrival time, displays error if it's wrong
        int arrivalTime;
        try {
            arrivalTime = Integer.parseInt(person[0]);
            if (arrivalTime < 1 || arrivalTime > 12)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            gp.writeError(g, NumberFormatException);
            return;
        }
        lblEntryTime.setText("Entry Time: " + arrivalTime + ":00");

        //retrieves the specific route of the person (either R for Registration or L for License)
        char route = person[1].charAt(0);
        if (route != 'R' && route != 'L') {
            gp.writeError(g, ImproperRoute);
            return;
        }

        //retrieves the payment method of the person (either $ for cash or C for check)
        char payment = person[2].charAt(0);
        if (payment != '$' && payment != 'C') {
            gp.writeError(g, ImproperPayment);
            return;
        }

        //retrieves the name of the person. GraphicsProcessor can only handle up to length 11, because of the pixel length of that many chars
        String name = person[3];
        if (name.length() > 11) {
            gp.writeError(g, NameTooLong);
            return;
        }

        //reset the GraphicsProcessor. This erases any person that previously went though
        gp = new GraphicsProcessor(g, name);

        //the main thread that progresses the person through the waiting graphic. Needs to be in seperate thread in order to allow the main thread to run during sleep
        Thread thread = new Thread() {
            public void run() {
                try {
                    //elapsed time = number of minutes that have elapsed thusfar, count = generic countdown for waiting
                    int elapsedTime = 0, count;

                    //sign in
                    for (count = waitTimeAtSignIn; count >= 0; count--) {
                        gp.setSignInTime(g, count);
                        Thread.sleep(waitTime);
                    }
                    elapsedTime += waitTimeAtSignIn;

                    //proceed to registration or license depending on person
                    if (route == 'R') {
                        gp.fromEntryToRegistration(g);

                        for (count = waitTimeAtRegistration; count >= 0; count--) {
                            gp.setRegTime(g, count);
                            Thread.sleep(waitTime);
                        }
                        elapsedTime += waitTimeAtRegistration;

                        gp.fromRegistrationToCashier(g);
                    } else {
                        gp.fromEntryToLicense(g);

                        for (count = waitTimeAtLicense; count >= 0; count--) {
                            gp.setLicTime(g, count);
                            Thread.sleep(waitTime);
                        }
                        elapsedTime += waitTimeAtLicense;

                        gp.fromLicenseToCashier(g);
                    }

                    //if paying via check, go verify the check. If not, skip this step
                    if (payment == 'C') {
                        for (count = waitTimeAtCashierWithCheck; count >= 0; count--) {
                            gp.setCashTime(g, count);
                            Thread.sleep(waitTime);
                        }
                        elapsedTime += waitTimeAtCashierWithCheck;

                        gp.fromCashierToCheckApprover(g);

                        for (count = waitTimeAtCheck; count >= 0; count--) {
                            gp.setCheckTime(g, count);
                            Thread.sleep(waitTime);
                        }
                        elapsedTime += waitTimeAtCheck;

                        gp.fromCheckApproverToCashier(g);
                    }

                    //pay and proceed to exit
                    for (count = waitTimeAtCashierToPay; count >= 0; count--) {
                        gp.setCashTime(g, count);
                        Thread.sleep(waitTime);
                    }
                    elapsedTime += waitTimeAtCashierToPay;

                    gp.fromCashierToExit(g);

                    //update the labels. This code needed because current thread is not same thread that JavaFX components are running on
                    final int elapsedTime2 = elapsedTime;
                    Platform.runLater(() -> {
                        lblElapsedTime.textProperty().setValue("Elapsed Time: " + elapsedTime2);
                        lblExitTime.textProperty().setValue("Exit Time: " + ((arrivalTime + (elapsedTime2 / 60)) % 12) + ":" + (elapsedTime2 % 60));
                    });
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException in start method of MainWindowController.java");
                }
            }
        };
        thread.start();
    }

}
