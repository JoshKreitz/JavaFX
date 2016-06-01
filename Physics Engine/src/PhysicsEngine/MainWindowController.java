package PhysicsEngine;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.*;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ResourceBundle;

//This class is where the bulk of the program takes place. It handles everything from the gui and at the same time
//calculates and maintains the "physics" of the ball.
public class MainWindowController implements Initializable {

//THESE VALUES MADE TO BE EDITTED

    //the starting coordinates of the ball
    private final double BALL_STARTING_X = 425;
    private final double BALL_STARTING_Y = 100;

    //the starting velocity of the ball
    private final double BALL_STARTING_X_VELOCITY = 0;
    private final double BALL_STARTING_Y_VELOCITY = 0;

    //the "gravity" of the ball -- how fast it falls to the bottom
    private DoubleProperty GRAVITY_ACCELERATION = new SimpleDoubleProperty(37.039);

    //the "friction" as the ball rolls across the bottom of the screen
    private DoubleProperty FRICTION_DECELERATION = new SimpleDoubleProperty(5);

    //how quickly the different threads refresh. Beware hardware dependencies
    private final double MAIN_THREAD_REFRESH_DELAY = 10;
    private final double MOUSE_TRACKER_REFRESH_DELAY = 50;

    //The velocity recorded from the mouse is often much too large. It is divided by this number
    private final double MOUSE_VELOCITY_DIVISOR = 2;

//END EDITABLE VALUES

    //canvas, the area where the ball can go
    public Canvas canvas;
    //the canvas' graphics, used to display the ball
    private GraphicsContext g;

    //the fxml components across the top of the program
    public HBox toolbar;
    public TextField xVelTextBox;
    public TextField yVelTextBox;
    public Slider gravitySlider;
    public Slider frictionSlider;

    //the offset between the mouse and the top left corner of the ball image
    private double xOffset, yOffset;
    //whether or not the mouse is hovering over the ball. Used to activate the listeners
    private boolean mouseInsideBall = false;

    //The ball itself, see Ball.java
    private Ball ball;
    //The amount of time the ball has been falling, used to calculate the change in velocity due to gravity
    private int timeFalling = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //allows the canvas to resize
        canvas.heightProperty().bind(Main.windowHeight.subtract(toolbar.getHeight() + 100));
        canvas.widthProperty().bind(Main.windowWidth.subtract(20));
        canvas.heightProperty().addListener(e -> {
            if (runMainThread) {
                yStopped1 = yStopped2 = false;
            }
            else {
                stopMainThread();
                try {
                    Thread.sleep((int) MAIN_THREAD_REFRESH_DELAY);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                startMainThread();
            }
        });

        //bind the variables to the slider values
        GRAVITY_ACCELERATION.bind(gravitySlider.valueProperty());
        FRICTION_DECELERATION.bind(frictionSlider.valueProperty());

        //clears the canvas
        g = canvas.getGraphicsContext2D();

        //creates the ball
        ball = new Ball(new Image("PhysicsEngine/ball.png"), BALL_STARTING_X, BALL_STARTING_Y, BALL_STARTING_X_VELOCITY, BALL_STARTING_Y_VELOCITY);
        g.drawImage(ball.getImg(), ball.getX(), ball.getY());

        //if ball is clicked, begin tracking its movement. Stop main thread if it is running already
        canvas.setOnMousePressed(e -> {
            if (ball.inside(e.getX(), e.getY())) {
                xOffset = ball.getX() - e.getX();
                yOffset = ball.getY() - e.getY();
                mouseInsideBall = true;

                startMouseTracker();
                stopMainThread();
            }
            else mouseInsideBall = false;
        });
        //allows the ball to be moved with the mouse, keeping it in the bounds of the canvas
        canvas.setOnMouseDragged(e -> {
            if (mouseInsideBall) {
                g.clearRect(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());

                if (e.getX() + xOffset < 0)
                    ball.setX(0);
                else if ((e.getX() + (ball.getWidth() + xOffset)) > canvas.getWidth())
                    ball.setX(canvas.getWidth() - ball.getWidth());
                else ball.setX(e.getX() + xOffset);

                if (e.getY() + yOffset < 0)
                    ball.setY(0);
                else if ((e.getY() + (ball.getHeight() + yOffset)) > canvas.getHeight())
                    ball.setY(canvas.getHeight() - ball.getHeight());
                else ball.setY(e.getY() + yOffset);

                g.drawImage(ball.getImg(), ball.getX(), ball.getY());
            }
        });
        //calculates the velocity of the mouse and applies it to the ball, then begins the main thread
        canvas.setOnMouseReleased(e -> {
            if (mouseInsideBall) {
                stopMouseTracker();

                ball.setxVelocity((((mouseThisX - mouseLastX) / MOUSE_TRACKER_REFRESH_DELAY) * 1000) / MOUSE_VELOCITY_DIVISOR);
                ball.setyVelocity((((mouseThisY - mouseLastY) / MOUSE_TRACKER_REFRESH_DELAY) * 1000) / MOUSE_VELOCITY_DIVISOR);

                startMainThread();
            }
        });

        //begins the main thread when the program launches
        startMainThread();
    }

    //controls whether or not the main thread runs
    private static boolean runMainThread;
    //determines if the ball is resting on the bottom of the screen. If two revolutions of main thread remain in same y position, it is
    private boolean yStopped1, yStopped2;


    //THE MAIN THREAD -- Controls all of the physics and movement of the ball
    //this indirect starting method only needed because resetting x velocity should NOT reset timeFalling, everything else should
    private void startMainThread() {
        timeFalling = 0;
        beginMainThread();
    }

    private void beginMainThread() {
        runMainThread = true;
        yStopped1 = yStopped2 = false;

        Thread mainThread = new Thread() {
            public void run() {
                while (runMainThread) {
                    g.clearRect(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());

                    //puts velocity in units of the refresh delay
                    double xVelocity = ball.getxVelocity() / (1000 / MAIN_THREAD_REFRESH_DELAY);
                    double yVelocity = ball.getyVelocity() / (1000 / MAIN_THREAD_REFRESH_DELAY);

                    //moves ball, keeping it within bounds of canvas, detecting collisions

                    if (ball.getX() + xVelocity < 0) {
                        ball.setX(0);
                        hitSideHorizontal();
                    }
                    else if (ball.getX() + ball.getWidth() + xVelocity > canvas.getWidth()) {
                        ball.setX(canvas.getWidth() - ball.getWidth());
                        hitSideHorizontal();
                    }
                    else ball.setX(ball.getX() + xVelocity);

                    if (yStopped2) {
                        if (ball.getxVelocity() == 0)
                            stopMainThread();

                        if (ball.getxVelocity() < 0)
                            ball.setxVelocity(ball.getxVelocity() + FRICTION_DECELERATION.get());
                        else
                            ball.setxVelocity(ball.getxVelocity() - FRICTION_DECELERATION.get());
                    }
                    else {
                        if (ball.getY() + yVelocity < 0) {
                            ball.setY(0);
                            hitSideVertical();
                            yStopped1 = false;
                        }
                        else if (ball.getY() + ball.getHeight() + yVelocity > canvas.getHeight()) {
                            ball.setY(canvas.getHeight() - ball.getHeight());
                            hitSideVertical();

                            if (!yStopped1)
                                yStopped1 = true;
                            else yStopped2 = true;

                        }
                        else {
                            ball.setY(ball.getY() + yVelocity);
                            yStopped1 = false;
                        }

                        //GRAVITY
                        ball.setyVelocity(ball.getyVelocity() + ((GRAVITY_ACCELERATION.get() * (timeFalling)) / 1000));
                    }

                    g.drawImage(ball.getImg(), ball.getX(), ball.getY());

                    try {
                        Thread.sleep((int) MAIN_THREAD_REFRESH_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    timeFalling += MAIN_THREAD_REFRESH_DELAY;
                }
            }
        };
        mainThread.start();
    }

    //stops the main thread
    public static void stopMainThread() {
        runMainThread = false;
    }

    //records the current and past position of the mouse, this can be used to calculate velocity
    private static boolean runMouseTracker = false;
    private double mouseLastX, mouseLastY;
    private double mouseThisX, mouseThisY;

    private void startMouseTracker() {
        runMouseTracker = true;

        Thread mouseTracker = new Thread() {
            public void run() {
                while (runMouseTracker) {
                    mouseLastX = mouseThisX;
                    mouseLastY = mouseThisY;

                    mouseThisX = MouseInfo.getPointerInfo().getLocation().getX();
                    mouseThisY = MouseInfo.getPointerInfo().getLocation().getY();

                    try {
                        Thread.sleep((int) MOUSE_TRACKER_REFRESH_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mouseTracker.start();
    }

    public static void stopMouseTracker() {
        runMouseTracker = false;
    }

    //hit left or right side of canvas
    private void hitSideHorizontal() {
        ball.setxVelocity(ball.getxVelocity() * -1);
    }

    //hit top or bottom of canvas
    private void hitSideVertical() {
        ball.setyVelocity(ball.getyVelocity() * -1);
    }

    //stops the ball entirely
    public void stopBall() {
        ball.setxVelocity(0);
        ball.setyVelocity(0);
        stopMainThread();
    }

    //sets the x velocity of the ball
    public void setXVelocity() {
        try {
            stopMainThread();
            Thread.sleep((int) MAIN_THREAD_REFRESH_DELAY);
            ball.setxVelocity(Integer.parseInt(xVelTextBox.getText()));
            beginMainThread();
            System.out.println(ball.getyVelocity());
        } catch (NumberFormatException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        xVelTextBox.setText("");
    }

    //sets the y velocity of the ball
    public void setYVelocity() {
        try {
            stopMainThread();
            Thread.sleep((int) MAIN_THREAD_REFRESH_DELAY);
            ball.setyVelocity(Integer.parseInt(yVelTextBox.getText()));
            startMainThread();
        } catch (NumberFormatException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        yVelTextBox.setText("");
    }
}
