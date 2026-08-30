package org.firstinspires.ftc.teamcode.katchup;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mechanism.Mechanism;

public class Drivetrain extends Mechanism {
    SwervePod p1;
    SwervePod p2;
    SwervePod heading;

    Pose poseEstimate = new Pose(0, 0, 0);
    private double translationLastTicks = 0;
    private double headingLastRadians = 0;

    public Drivetrain(HardwareMap hw, Telemetry tele) {
        super(hw, tele);

        p1 = new SwervePod(hw, tele, "sw1");
        p2 = new SwervePod(hw, tele, "sw2");
        heading = new SwervePod(hw, tele, "h3");

        PIDFCoefficients defaultCoeffs = new PIDFCoefficients(0.1, 0, 0, 0);
        p1.setCoefficients(defaultCoeffs);
        p2.setCoefficients(defaultCoeffs);
        heading.setCoefficients(defaultCoeffs);

        p1.setTicksPerInches(1892.42);
        p2.setTicksPerInches(1892.42);

        // 480 ticks per 2*pi radians (Modern Robotics 480 CPR empirical setup)
        heading.setTicksPerInches(456 / (2.0 * Math.PI));

        // Seed initial tick and angle values to prevent startup odometry jumps
        translationLastTicks = p1.getCurrentPosition();
        headingLastRadians = getCurrentHeadingRadians();

        displayTelemetry("Pose X", poseEstimate::getX);
        displayTelemetry("Pose Y", poseEstimate::getY);
        displayTelemetry("Pose Heading", ()->Math.toDegrees(poseEstimate.getHeading()));
        displayTelemetry("P1 Target", p1::getTargetPosition);
        displayTelemetry("P1 COR", p1::getCurrentPosition);
        displayTelemetry("P1 Error", p1.controller::getError);
        displayTelemetry("Heading Ticks", heading::getCurrentPosition);
    }

    public void setPoseEstimate(Pose e) {
        this.poseEstimate = e;
    }

    private double toInches(SwervePod pod, double ticks) {
        return ticks / pod.getTicksPerInches();
    }

    private double getCurrentHeadingRadians() {
        return heading.getCurrentPosition() / heading.getTicksPerInches();
    }

    public static double wrapAngle(double radians) {
        return Math.atan2(Math.sin(radians), Math.cos(radians));
    }

    /**
     * Sets target angle in radians using continuous shortest-path targeting.
     */
    public void setHeadingTargetRadians(double targetRadians) {
        double ticksPerRad = heading.getTicksPerInches();
        if (ticksPerRad == 0) return;

        double currentRadians = getCurrentHeadingRadians();
        double diffRad = Math.IEEEremainder(targetRadians - currentRadians, 2.0 * Math.PI);
        double optimizedTargetRadians = currentRadians + diffRad;

        heading.setTargetPosition(optimizedTargetRadians * ticksPerRad);
    }

    public void updatePoseEstimate() {
        double currentTranslationTicks = p1.getCurrentPosition();
        double currentHeadingRadians = getCurrentHeadingRadians();

        // Calculate deltas
        double deltaTranslationTicks = currentTranslationTicks - translationLastTicks;
        double translationChangeInches = toInches(p1, deltaTranslationTicks);

        // Average heading integration for arc displacement approximation
        double avgHeadingRadians = headingLastRadians + ((currentHeadingRadians - headingLastRadians) / 2.0);

        double xComponent = Math.cos(avgHeadingRadians);
        double yComponent = Math.sin(avgHeadingRadians);

        poseEstimate.setX(poseEstimate.getX() + (xComponent * translationChangeInches));
        poseEstimate.setY(poseEstimate.getY() + (yComponent * translationChangeInches));
        poseEstimate.setHeading(wrapAngle(currentHeadingRadians));

        // Save states for next frame
        translationLastTicks = currentTranslationTicks;
        headingLastRadians = currentHeadingRadians;
    }

    @Override
    public void loop() {
//        p1.loop();
//        p2.loop();
        heading.loop();

        updatePoseEstimate();
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        if (g1.bWasPressed()) {
            setPoseEstimate(new Pose(0, 0, 0));
        }

        // Action triggers overriding stick positions
        if (g1.xWasPressed()) {
            p1.setTargetPosition(500);
            p2.setTargetPosition(500);
        } else if (g1.yWasPressed()) {
            p1.setTargetPosition(1000);
            p2.setTargetPosition(1000);
        } else if (Math.abs(g1.left_stick_y) > 0.05) {
            // Incremental stick mapping scaling directly to stick displacement
            p1.setTargetPosition(p1.getTargetPosition() + (-g1.left_stick_y * 10));
            p2.setTargetPosition(p2.getTargetPosition() + (-g1.left_stick_y * 10));
        }

        if (g1.aWasPressed()) {
            heading.setTargetPosition(500);
        }

        // Steering control: Map joystick to standard coordinates (+X = Forward, +Y = Left)
        double robotForward = -g1.left_stick_y;
        double robotLeft    = -g1.left_stick_x;

        if (Math.hypot(robotForward, robotLeft) > 0.1) {
            double targetRadians = Math.atan2(robotLeft, robotForward);
            setHeadingTargetRadians(targetRadians);
        }
    }
}