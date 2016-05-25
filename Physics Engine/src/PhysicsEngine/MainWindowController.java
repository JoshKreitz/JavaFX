package PhysicsEngine;

import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable{

    public Canvas canvas;
    private GraphicsContext g;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        g = canvas.getGraphicsContext2D();
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.setFill(Color.WHITE);
        g.fillRect(10, 10, canvas.getWidth()-20, canvas.getHeight()-20);
    }
}
