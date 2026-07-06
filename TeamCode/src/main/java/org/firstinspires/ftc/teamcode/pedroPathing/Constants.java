package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.CoaxialPod;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants();

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    // adapted: https://pedropathing.com/docs/pathing/tuning/swerve/swerve-setup
    private static final double dtX = 10;
    private static final double dtY = 10;

    private static CoaxialPod frontLeft(HardwareMap hardwareMap) {
        return new CoaxialPod(
                hardwareMap,
                "frontLeftMotor", //the name of your motor in your config
                "frontLeftServo", //the name of your servo in your config
                "frontLeftEncoder", // the name of your analog encoder in your config
                new PIDFCoefficients(0.3, 0, 0.005, 0.01), //pod PIDF coefficients
                DcMotorSimple.Direction.FORWARD, //the direction of your motor
                DcMotorSimple.Direction.FORWARD, //the direction of your servo
                Math.toRadians(353.1), //your pod's angle offset, in radians
                new Pose(dtX, dtY), //your pods x and y offsets,
                0, //analog min voltage
                3.3, //analog max voltage
                false);
    }
    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .build();
    }
}