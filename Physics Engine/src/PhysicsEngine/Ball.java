package PhysicsEngine;

import javafx.geometry.BoundingBox;
import javafx.scene.image.Image;

//This class holds the different values and attributes of the ball at any given moment
public class Ball {

    //the x and y position of the top left corner of the image
    private double x, y;

    //the velocity of the ball measured in px/sec
    private double xVelocity, yVelocity;

    //the image of the ball
    private Image img;

    //the bounds of the ball
    private BoundingBox bounds;

    public Ball(Image img) {
        this.img = img;
        bounds = new BoundingBox(0, 0, img.getWidth(), img.getHeight());
        x = y = xVelocity = yVelocity = 0;
    }

    public Ball(Image img, double x, double y) {
        this.img = img;
        this.x = x;
        this.y = y;
        bounds = new BoundingBox(x, y, img.getWidth(), img.getHeight());
        xVelocity = yVelocity = 0;
    }

    public Ball(Image img, double x, double y, double xVelocity, double yVelocity) {
        this.img = img;
        this.x = x;
        this.y = y;
        bounds = new BoundingBox(x, y, img.getWidth(), img.getHeight());
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        bounds = new BoundingBox(x, y, img.getWidth(), img.getHeight());
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        bounds = new BoundingBox(x, y, img.getWidth(), img.getHeight());
    }

    public double getxVelocity() {
        return xVelocity;
    }

    public void setxVelocity(double xVelocity) {
        this.xVelocity = xVelocity;
    }

    public double getyVelocity() {
        return yVelocity;
    }

    public void setyVelocity(double yVelocity) {
        this.yVelocity = yVelocity;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
        bounds = new BoundingBox(x, y, img.getWidth(), img.getHeight());
    }

    public double getHeight() {
        return img.getHeight();
    }

    public double getWidth() {
        return img.getWidth();
    }

    public boolean inside(double x, double y) {
        return bounds.contains(x, y);
    }
}
