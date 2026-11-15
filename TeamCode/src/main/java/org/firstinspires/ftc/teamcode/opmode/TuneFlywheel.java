package org.firstinspires.ftc.teamcode.opmode;

import androidx.collection.ArraySet;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.mechanism.Intake;
import org.firstinspires.ftc.teamcode.mechanism.Robot;
import org.firstinspires.ftc.teamcode.mechanism.Shooter;
import org.firstinspires.ftc.teamcode.mechanism.Transfer;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp(name = "Flywheel Tuner")
public class TuneFlywheel extends OpMode {
    private Robot robot;
    TelemetryManager telemetryM;
    ArraySet<double[]> doubleList;

    float rpm = 1000;
    float add = 100;

    @Override
    public void init() {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        doubleList = new ArraySet<>();

        robot = new Robot(hardwareMap, telemetry);
        robot.follower().setPose(new Pose(58, 10, Math.toRadians(0)));

        telemetryM.update(telemetry);
    }


    @Override
    public void loop() {
        /// DRIVE

        Globals.lastPose = robot.follower().pose();

        double forward = -gamepad1.left_stick_y;
        double lateral = -gamepad1.left_stick_x;
        double turn = -gamepad1.right_stick_x;

        robot.follower().manual(forward,lateral,turn);

        /// CONTROLS

        boolean triggerPressed = gamepad1.right_trigger > 0.7;
        boolean shoulderPressed = gamepad1.right_bumper;
        boolean shouldIntake = shoulderPressed && !triggerPressed;

        if(triggerPressed) {
            if(robot.shooter().flywheelAtTarget()) robot.transfer().transfer();
            else robot.transfer().stop();
        } else {
            robot.transfer().stop();
        }

        if(shouldIntake) {
            robot.intake().intake();
            robot.transfer().runFrontOnly();
        } else if(gamepad1.left_bumper) {
            robot.transfer().outtake();
        } else {
            robot.intake().stop();
            if(!triggerPressed) robot.transfer().stop();
        }


        if(gamepad1.dpadRightWasPressed()) {
            add += 50;
        }
        if(gamepad1.dpadLeftWasPressed()) {
            add -= 50;
        }
        if(gamepad1.dpadUpWasPressed()) {
            rpm += add;
        }
        if(gamepad1.dpadDownWasPressed()) {
            rpm -= add;
        }

        if(gamepad1.crossWasPressed()) {
            doubleList.add(new double[] {Globals.redBasketAudienceGoal.distance(robot.currentPose()), rpm});
        }
        robot.shooter().setVelocity(rpm);

        // TELEMETRY / LOOP

        telemetryM.addData("Edit Size", add);
        telemetryM.addData("Distance to goal", robot.currentPose().distance(Globals.redBasketAudienceGoal));
        telemetryM.addData("Velocity", rpm);
        telemetryM.addData("Current Shooter Velocity", robot.shooter().getVelocity());
        telemetryM.addLine("G1 Dpad LEFT : -50tps from edit size");
        telemetryM.addLine("G1 Dpad RIGHT : +50tps from edit size");
        telemetryM.addLine("G1 Dpad UP : +" + add+"tps");
        telemetryM.addLine("G1 Dpad DOWN : -" + add +"tps");
        for(double[] i : doubleList) {
            telemetryM.addLine("{" + i[0] + ", " + i[1]+"},");
        }

        robot.loop();
        robot.showDebugInfoOn(telemetryM);
        telemetryM.update(telemetry);
    }
}
