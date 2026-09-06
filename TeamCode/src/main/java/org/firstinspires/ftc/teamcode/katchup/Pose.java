package org.firstinspires.ftc.teamcode.katchup;

public class Pose {
    public double x, y, heading; // heading in radians

    public Pose(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
    }

    public double distance(Point other) {
        return Math.hypot(other.x - this.x, other.y - this.y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeading() {
        return heading;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }
}
