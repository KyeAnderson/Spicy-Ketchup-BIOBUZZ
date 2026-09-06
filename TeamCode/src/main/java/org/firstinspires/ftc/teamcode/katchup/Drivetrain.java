package org.firstinspires.ftc.teamcode.katchup;

import java.util.ArrayList;
import java.util.List;

public class Drivetrain {
    List<SwervePod> drivePods = new ArrayList<>();
    SwervePod steeringPod;
    private double translationLastTicks = 0;

    public void setupDrivePods(List<SwervePod> drives, SwervePod steeringPod) {
        drivePods.addAll(drives);
        this.steeringPod = steeringPod;
    }

    public double getCurrentHeadingRadians() {
        return steeringPod.getCurrentPosition() / steeringPod.getTicksPerUnit();
    }

    public void setHeadingTargetRadians(double targetRadians) {
        double ticksPerRad = steeringPod.getTicksPerUnit();
        if (ticksPerRad == 0) return;

        double currentRadians = getCurrentHeadingRadians();
        double diffRad = Math.IEEEremainder(targetRadians - currentRadians, 2.0 * Math.PI);
        double optimizedTargetRadians = currentRadians + diffRad;

        steeringPod.setTargetPosition(optimizedTargetRadians * ticksPerRad);
    }

    public void loop() {
        steeringPod.loop();
        for (SwervePod pod : drivePods) pod.loop();
    }

    public void update(DrivetrainResponseCommand command) {
        double targetSteerAngle = command.getSteeringAngle();
        setHeadingTargetRadians(targetSteerAngle);
        for(SwervePod pod : drivePods) {
            pod.setVelocity(command.getDrivePower() * pod.getTicksPerUnit());
        }
    }

    public double averageTranslation() {
        double avg = 0;
        for(SwervePod pod : drivePods) {
            avg += pod.getCurrentPosition();
        }
        return avg / drivePods.size();
    }

    public void updatePoseEstimate(Localizer localizer) {
        double currentTranslationTicks = averageTranslation();
        double currentHeadingRadians = getCurrentHeadingRadians();

        double deltaTranslationTicks = currentTranslationTicks - translationLastTicks;
        double translationChangeInches = deltaTranslationTicks / drivePods.get(0).getTicksPerUnit();

        localizer.update(translationChangeInches, 0, currentHeadingRadians);

        translationLastTicks = currentTranslationTicks;
    }

    // pure pursuit
    public static class DrivetrainResponseCommand {
        public double steeringAngle;
        public double curvature;
        public double drivePower;

        public DrivetrainResponseCommand(double steeringAngle, double curvature, double drivePower) {
            this.steeringAngle = steeringAngle;
            this.curvature = curvature;
            this.drivePower = drivePower;
        }

        public double getSteeringAngle() { return steeringAngle; }
        public double getCurvature() { return curvature; }
        public double getDrivePower() { return drivePower; }
    }

    double trackWidth; // distance from left to right
    double wheelBase; // distance from front to back
    double maxVelocity;
    double lookaheadDistance;
    double minimumVelocityFactor = 0.2;
    Pose currentPosition;

    public Drivetrain(double trackWidth, double wheelBase, double maxVelocity, double lookaheadDistance) {
        this.wheelBase = wheelBase;
        this.trackWidth = trackWidth;
        this.maxVelocity = maxVelocity;
        this.lookaheadDistance = lookaheadDistance;
        currentPosition = new Pose(0, 0, Math.toRadians(0));
    }

    public void setCurrentPosition(Pose p) {
        this.currentPosition = p;
    }

    public DrivetrainResponseCommand followAlongPath(Path path) {
        return determineDrivetrainVelocity(path, currentPosition, lookaheadDistance);
    }

    public static double wrapAngle(double radians) {
        return Math.atan2(Math.sin(radians), Math.cos(radians));
    }

    public DrivetrainResponseCommand determineDrivetrainVelocity(Path path, Pose pose, double lookaheadDist) {
        List<Point> pts = path.getPointsList();

        int closestIndex = 0;
        double minDist = pose.distance(pts.get(0));
        for (int i = 1; i < pts.size(); i++) {
            double d = pose.distance(pts.get(i));
            if (d < minDist) {
                minDist = d;
                closestIndex = i;
            }
        }

        Point goalPoint = null;
        for (int i = closestIndex; i < pts.size(); i++) {
            if (pose.distance(pts.get(i)) >= lookaheadDist) {
                goalPoint = pts.get(i);
                break;
            }
        }
        if (goalPoint == null) goalPoint = pts.get(pts.size() - 1);

        double dx = goalPoint.x - pose.x;
        double dy = goalPoint.y - pose.y;
        double localX = dx * Math.cos(-pose.heading) - dy * Math.sin(-pose.heading);
        double localY = dx * Math.sin(-pose.heading) + dy * Math.cos(-pose.heading);

        double l_d = Math.hypot(localX, localY);
        double curvature = 0.0;
        if (l_d >= 1e-6) {
            curvature = (2.0 * localY) / (l_d * l_d);
        }

        double ppSteeringAngle = Math.atan2(localY, localX);

        Point finalPoint = pts.get(pts.size() - 1);
        double distanceToEnd = pose.distance(finalPoint);

        double steeringAngle;
        double blendDistance = lookaheadDist * 1.5;

        double headingWeight = 1.0 - (distanceToEnd / blendDistance);
        headingWeight = Math.min(Math.max(headingWeight, 0.0), 1.0);

        double targetHeading = path.getFinalHeading();
        double headingError = targetHeading - pose.heading;

        while (headingError > Math.PI) headingError -= 2 * Math.PI;
        while (headingError < -Math.PI) headingError += 2 * Math.PI;

        steeringAngle = (1.0 - headingWeight) * ppSteeringAngle + headingWeight * headingError;
        if (distanceToEnd < lookaheadDist) {
            steeringAngle = headingError;
        }
        double decelerationDistance = trackWidth * lookaheadDist;

        double velocityScale = Math.min(Math.max(distanceToEnd / decelerationDistance, minimumVelocityFactor), 1.0);
        double targetVelocity = maxVelocity * velocityScale;
        if (distanceToEnd <= 0.3) {
            targetVelocity = 0.0;
        }
        // Wrap angle, because we expect an absolute angle to rotate wheels to
        return new DrivetrainResponseCommand(wrapAngle( pose.heading + steeringAngle), curvature, targetVelocity);
    }
    public boolean hasFinishedPath(Path path, Pose pose, double tolerance) {
        Point finalPoint = path.getPointsList().get(path.getPointsList().size() - 1);
        double distanceToGoal = pose.distance(finalPoint);
        return (distanceToGoal <= tolerance) && (Math.abs(pose.heading - path.getFinalHeading()) < Math.toRadians(3));
    }
}