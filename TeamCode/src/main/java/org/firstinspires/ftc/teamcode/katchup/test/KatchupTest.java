package org.firstinspires.ftc.teamcode.katchup.test;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.katchup.Drivetrain;
import org.firstinspires.ftc.teamcode.katchup.Katchup;
import org.firstinspires.ftc.teamcode.katchup.Path;
import org.firstinspires.ftc.teamcode.katchup.Point;
import org.firstinspires.ftc.teamcode.katchup.SwervePod;

import java.util.Arrays;

@Autonomous(name = "katchup test", group = "Swerve")
public class KatchupTest extends OpMode {
    Katchup katchup = new Katchup(10, 5, 20, 2.5);
    Path testPath = new Path.Builder(
            new Point(0, 0),
            new Point(15, 5)
    ).setFinalHeading(Math.toRadians(180)).build();
    PIDFCoefficients defaultCoeffs = new PIDFCoefficients(0.1, 0, 0, 0);

    @Override
    public void init() {
        katchup.getDrivetrain().setupDrivePods(
                Arrays.asList(
                        new SwervePod(hardwareMap, telemetry, "sw1", defaultCoeffs, SwervePod.ControlMode.VELOCITY, 1892.42),
                        new SwervePod(hardwareMap, telemetry, "sw2", defaultCoeffs, SwervePod.ControlMode.VELOCITY, 1892.42)
                ),
                new SwervePod(hardwareMap, telemetry, "h3", defaultCoeffs, SwervePod.ControlMode.POSITION, (28 * 27 * (12.0/7.0)) / (2.0 * Math.PI))
        );
    }

    @Override
    public void start() {
        katchup.followPath(testPath);
    }

    @Override
    public void loop() {
        katchup.update();

        telemetry.addData("Status", "Following Path...");
        Drivetrain.DrivetrainResponseCommand cmd = katchup.getDrivetrain().followAlongPath(testPath);
        telemetry.addData("Steer Angle (deg)", Math.toDegrees(cmd.getSteeringAngle()));
        telemetry.addData("Drive Power", cmd.getDrivePower());
        telemetry.update();

    }
}