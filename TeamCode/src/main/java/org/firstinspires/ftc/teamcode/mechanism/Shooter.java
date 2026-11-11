package org.firstinspires.ftc.teamcode.mechanism;

import com.arcrobotics.ftclib.util.InterpLUT;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.helpers.HoodCompression;

import java.util.Arrays;
import java.util.Comparator;

public class Shooter extends Mechanism {
    public static final int TESTING_SHOOTER_VELO = 1650;
    public static final int GOOD_VELO_FOUND = 2145; // this value gud
    DcMotorEx flywheel;
    Servo angle;
    HoodCompression compress;
    float velocity;

    InterpLUT velocities;

    double[][] distanceToGoalToRPM = {
            {0, 1650.0},
            {30.13058603493952, 1700.0},

            {39.96349192038742, 1650.0},

            {43.99638393504309, 1800.0},

            {30.1274409546165, 1700.0},

            {43.99605856189457, 1750.0},

            {30.1388844686428, 1750.0},
    };

    public Shooter(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        Arrays.sort(distanceToGoalToRPM, Comparator.comparingDouble(a -> a[0]));
        velocities = new InterpLUT();

        for(double[] pair : distanceToGoalToRPM) {
            if(pair.length == 0 || pair.length == 1) {
                throw new Error("Missing a pair of power!!");
            }
            velocities.add(pair[0], pair[1]);
        }

        velocities.createLUT();

        flywheel = hm.get(DcMotorEx.class, "fly");
        angle = hm.get(Servo.class, "ang");
        velocity = 0;
        compress = HoodCompression.POLLEN;
        initializeMotors(flywheel);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);

        displayTelemetry("Hood Compression", ()->compress);
        displayTelemetry("Hood Angle", angle::getPosition);
        displayTelemetry("Flywheel velocity", flywheel::getVelocity);
    }

    public double getVelocityFor(double distance){
        double minDistance = distanceToGoalToRPM[0][0]+.01;
        double maxDistance = distanceToGoalToRPM[distanceToGoalToRPM.length-1][0]-.02;

        double clampedDistance = Math.max(minDistance, Math.min(distance, maxDistance));
        return velocities.get(clampedDistance);
    }

    public boolean atVelocityFor(double distance) {
        double v= getVelocityFor(distance);
        double tolerance = 50;
        return Math.abs(getVelocity() - v) < tolerance;
    }

    public void setCompression(HoodCompression comp) {
        if(compress == comp) return;
        compress = comp;
    }

    public void setVelocity(float v) {
        if(velocity == v) return;
        velocity = v;
    }

    public boolean flywheelAtTarget() {
        return velocity != 0 && Math.abs(flywheel.getVelocity() - velocity) < 50;
    }

    public void stop() {
        setVelocity(0);
    }

    @Override
    public void loop() {
        flywheel.setVelocity(velocity);
        angle.setPosition(compress.getPosition());
    }

    public double getVelocity() {
        return flywheel.getVelocity();
    }

    public HoodCompression getCompress() {
        return compress;
    }

    double TestRPM = 2145;
    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        if(g1.squareWasPressed()) {
            setCompression(HoodCompression.POLLEN);
        }
        if(g1.circleWasPressed()) {
            setCompression(HoodCompression.NECTAR);
        }

        if(g1.dpadUpWasPressed()) {
            TestRPM += 5;
        }
        if(g1.dpadDownWasPressed()) {
            TestRPM -= 5;
        }

        setVelocity((float)TestRPM);
        telemetryM.addData("TestRPM", TestRPM);
        telemetryM.addLine("Press square to set HoodCompression.POLLEN");
        telemetryM.addLine("Press circle to set HoodCompression.NECTAR");
    }
}
