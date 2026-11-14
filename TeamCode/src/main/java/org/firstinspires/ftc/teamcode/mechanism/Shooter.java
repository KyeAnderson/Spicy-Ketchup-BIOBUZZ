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
import org.firstinspires.ftc.teamcode.helpers.HardwareConstants;

import java.util.Arrays;
import java.util.Comparator;

public class Shooter extends Mechanism {
    public static final int TESTING_SHOOTER_VELO = 2145;
    DcMotorEx flywheel;

    double setpointVelocity;

    // TODO: retune with new shooter
    double[][] distanceVelocityTable = {
            {0, 1650.0},
            {30.13058603493952, 1700.0},
            {39.96349192038742, 1650.0},
            {43.99638393504309, 1800.0},
            {30.1274409546165, 1700.0},
            {43.99605856189457, 1750.0},
            {30.1388844686428, 1750.0},
    };
    InterpLUT distanceVelocityLUT;

    public Shooter(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        setupLUT();

        flywheel = hm.get(DcMotorEx.class, HardwareConstants.SHOOTER_FLYWHEEL_MOTOR.getId());
        setpointVelocity = 0;
        initializeMotors(flywheel);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);

        displayTelemetry("Setpoint Velocity", ()->setpointVelocity);
        displayTelemetry("Current Velocity", flywheel::getVelocity);
    }

    void setupLUT() {
        // Add each dist->velo pair so FTCLib can generate LUT
        distanceVelocityLUT = new InterpLUT();

        // Inputting values into LUT must be in order, so this is a hacky fix for that
        Arrays.sort(distanceVelocityTable, Comparator.comparingDouble(a -> a[0]));
        for(double[] pair : distanceVelocityTable) {
            if(pair.length == 0 || pair.length == 1) {
                throw new Error("Missing a pair of distance -> velocity!!");
            }
            distanceVelocityLUT.add(pair[0], pair[1]);
        }

        distanceVelocityLUT.createLUT();
    }

    public double getVelocityForDist(double distance){
        double minDistance = distanceVelocityTable[0][0]+.01;
        double maxDistance = distanceVelocityTable[distanceVelocityTable.length-1][0]-.02;
        // the above makes sure you're within 0.1 < distance < (about) 45.02
        // this is a very hacky fix because LUT doesn't like 0, or going to the exact maximum value

        double clampedDistance = Math.max(minDistance, Math.min(distance, maxDistance));
        return distanceVelocityLUT.get(clampedDistance);
    }

    public boolean atVelocityForDist(double distance) {
        double v= getVelocityForDist(distance);
        double tolerance = 50;
        // are we within 50 tps of wanted velocity?
        return Math.abs(getVelocity() - v) < tolerance;
    }

    public void setVelocity(double v) {
        if(setpointVelocity == v) return;
        setpointVelocity = v;
    }

    public boolean flywheelAtTarget() {
        return setpointVelocity != 0 && Math.abs(flywheel.getVelocity() - setpointVelocity) < 50;
    }

    public void stop() {
        setVelocity(0);
    }

    @Override
    public void loop() {
        flywheel.setVelocity(setpointVelocity);
    }

    public double getVelocity() {
        return flywheel.getVelocity();
    }

    double testVelocity = 2145;
    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        if(g1.dpadUpWasPressed()) {
            testVelocity += 5;
        }
        if(g1.dpadDownWasPressed()) {
            testVelocity -= 5;
        }

        setVelocity(testVelocity);
        telemetryM.addLine("G1 / DPAD UP : add 5 to testVelocity");
        telemetryM.addLine("G1 / DPAD UP : subtract 5 from testVelocity");
        telemetryM.addData("testVelocity", testVelocity);
    }
}
