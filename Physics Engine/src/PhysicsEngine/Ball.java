package PhysicsEngine;

public class Ball {

    private double x, y;
    private double xVelocity, yVelocity;

    public Ball(){
        x = y = xVelocity = yVelocity = 0;
    }

    public Ball(double x, double y){
        this.x = x;
        this.y = y;
        xVelocity = yVelocity = 0;
    }

    public Ball(double x, double y, double xVelocity, double yVelocity){
        this.x = x;
        this.y = y;
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
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
}
