package PhysicsEngine;

import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable{

    public Canvas canvas;
    private GraphicsContext g;

    private double xOffset, yOffset;
    private boolean mouseInsideBall = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        g = canvas.getGraphicsContext2D();
        BoundingBox canvasBounds = new BoundingBox(0, 0, canvas.getWidth(), canvas.getHeight());

        Ball ball = new Ball(new Image("PhysicsEngine/ball.png"), 50, 50);
        g.drawImage(ball.getImg(), ball.getX(), ball.getY());

        canvas.setOnMousePressed(e -> {
            if(ball.inside(e.getX(), e.getY())){
                xOffset = ball.getX() - e.getX();
                yOffset = ball.getY() - e.getY();
                mouseInsideBall = true;
            }
            else mouseInsideBall = false;
        });
        canvas.setOnMouseDragged(e -> {
            if(mouseInsideBall){
                g.clearRect(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());

                if(canvasBounds.contains(e.getX() + xOffset, 0) && canvasBounds.contains(e.getX() + (ball.getWidth() + xOffset), 0))
                    ball.setX(e.getX() + xOffset);

                if(canvasBounds.contains(0, e.getY() + yOffset) && canvasBounds.contains(0, e.getY() + (ball.getHeight() + yOffset)))
                    ball.setY(e.getY() + yOffset);

                g.drawImage(ball.getImg(), ball.getX(), ball.getY());
            }
        });
    }
}
