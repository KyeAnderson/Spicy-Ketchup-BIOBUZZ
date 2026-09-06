package org.firstinspires.ftc.teamcode.katchup;

public class Katchup {
    private double finishTestTolerance = 0.3;
    private final Drivetrain drivetrain;
    private final Localizer localizer;
    private Path currentPath;

    public Katchup(
            double trackWidth,
            double wheelBase,
            double maxVelocity,
            double lookaheadRadius
    ) {
        drivetrain = new Drivetrain(trackWidth, wheelBase, maxVelocity, lookaheadRadius);
        localizer = new Localizer(0, 0, Math.toRadians(0));
    }

    public Drivetrain getDrivetrain() {
        return drivetrain;
    }

    public Localizer getLocalizer() {
        return localizer;
    }

    public void setFinishTestTolerance(double k) {
        finishTestTolerance = k;
    }

    public void followPath(Path p) {
        this.currentPath = p;
    }

    public void update() {
        if(this.currentPath == null) return;
        drivetrain.updatePoseEstimate(localizer);
        drivetrain.setCurrentPosition(localizer.getPose());
        if(this.currentPath != null && isBusy()) {
            drivetrain.update(drivetrain.followAlongPath(this.currentPath));
        }
        drivetrain.loop();
    }

    public boolean isBusy() {
        return currentPath != null && !drivetrain.hasFinishedPath(currentPath, localizer.getPose(), finishTestTolerance);
    }

    public void setCurrentPosition(Pose p) {localizer.reset(p.getX(), p.getY(), p.getHeading());}

    public Pose getCurrentPosition() {
        return localizer.getPose();
    }
}
