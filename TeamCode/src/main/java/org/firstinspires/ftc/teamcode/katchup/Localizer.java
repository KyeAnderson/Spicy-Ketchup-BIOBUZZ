package org.firstinspires.ftc.teamcode.katchup;

public class Localizer {
    private double globalX;
    private double globalY;
    private double globalHeading; // in radians

    private double lastForwardPosition = 0.0;
    private double lastLateralPosition = 0.0;

    public Localizer(double startX, double startY, double startHeading) {
        this.globalX = startX;
        this.globalY = startY;
        this.globalHeading = startHeading;
    }

    public void update(double deltaForward, double deltaLateral, double currentHeading) {
        double dHeading = currentHeading - globalHeading;

        double averageHeading = globalHeading + (dHeading / 2.0);

        double globalDx = deltaForward * Math.cos(averageHeading) - deltaLateral * Math.sin(averageHeading);
        double globalDy = deltaForward * Math.sin(averageHeading) + deltaLateral * Math.cos(averageHeading);

        globalX += globalDx;
        globalY += globalDy;
        globalHeading = currentHeading;
    }

    public Pose getPose() {
        return new Pose(globalX, globalY, globalHeading);
    }

    public void reset(double x, double y, double heading) {
        this.globalX = x;
        this.globalY = y;
        this.globalHeading = heading;
    }

    public double getLastLateralPosition() {
        return lastLateralPosition;
    }

    public void setLastLateralPosition(double lastLateralPosition) {
        this.lastLateralPosition = lastLateralPosition;
    }

    public double getLastForwardPosition() {
        return lastForwardPosition;
    }

    public void setLastForwardPosition(double lastForwardPosition) {
        this.lastForwardPosition = lastForwardPosition;
    }
}