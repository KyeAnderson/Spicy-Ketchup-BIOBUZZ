package org.firstinspires.ftc.teamcode.katchup;

public class Point {
    public double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Point p) {
        return Math.hypot(this.x - p.x, this.y - p.y);
    }
}
