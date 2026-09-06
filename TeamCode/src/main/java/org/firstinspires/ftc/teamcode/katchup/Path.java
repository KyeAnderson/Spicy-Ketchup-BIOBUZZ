package org.firstinspires.ftc.teamcode.katchup;

import java.util.LinkedList;
import java.util.List;

public class Path {
    public static class Builder {
        Point[] waypoints;
        Path path;
        public Builder(Point... waypoints) {
            this.waypoints = waypoints;
            this.path = new Path(waypoints);
        }
        public Builder setSpacing(double sp) {
            path.setSpacing(sp);
            return this;
        }

        public Builder setFinalHeading(double rad) {
            path.setFinalHeading(rad);
            return this;
        }

        public Path build() {
            return path;
        }
    }

    private final Point[] initialPoints;
    private LinkedList<Point> points;
    private double finalHeading; // Optional target end-heading in radians

    public Path(Point... waypoints) {
        this.initialPoints = waypoints;
        this.setSpacing(0.25); // also populates points
    }
    public Path(double spacing, Point... waypoints) {
        this.initialPoints = waypoints;
        this.setSpacing(spacing); // also populates points
    }

    public void setFinalHeading(double radians) {
        this.finalHeading = radians;
    }

    public void setSpacing(double sp) {
        this.points = new LinkedList<>();
        if (initialPoints.length == 0) return;

        if (initialPoints.length == 1) {
            this.points.add(initialPoints[0]);
            return;
        }

        for (int i = 0; i < initialPoints.length - 1; i++) {
            interpolateSegment(initialPoints[i], initialPoints[i + 1], sp);
        }
        this.points.add(initialPoints[initialPoints.length - 1]);
    }

    private void interpolateSegment(Point p1, Point p2, double spacing) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double distance = Math.hypot(dx, dy);

        // Determine how many steps fit into this segment based on spacing
        int steps = (int) Math.ceil(distance / spacing);

        for (int i = 0; i < steps; i++) {
            double t = (double) i / steps;
            double x = p1.x + t * dx;
            double y = p1.y + t * dy;
            this.points.add(new Point(x, y));
        }
    }

    public double getFinalHeading() {
        return finalHeading;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public List<Point> getPointsList() {
        return points.stream().toList();
    }

    public LinkedList<Point> getPoints() {
        return points;
    }
}