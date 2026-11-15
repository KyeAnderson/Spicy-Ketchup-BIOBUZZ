package org.firstinspires.ftc.teamcode.opmode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.mechanism.Robot;
import org.firstinspires.ftc.teamcode.mechanism.Shooter;

@TeleOp(name="Powell at League Meet 2: Hola")
public class SpicyTele extends OpMode {
    private Robot robot;
    private boolean showDebugInfo = true;
    private TelemetryManager telemetryM;

    @Override
    public void init() {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        robot = new Robot(hardwareMap, telemetry);
        robot.follower().setPose(new Pose(58,10,0));

        telemetryM.update(telemetry);
    }

    @Override
    public void loop() {
        /// DRIVE + CONTROLS

        double forward = -gamepad1.left_stick_y;
        double lateral = -gamepad1.left_stick_x;
        double turn = -gamepad1.right_stick_x;

        boolean shouldDynamicVelocity = gamepad2.right_trigger > 0.7 || gamepad1.right_trigger > 0.7; // Incase 2 disconnects
        boolean shouldStaticVelocity = gamepad2.left_trigger > 0.7 || gamepad1.left_trigger > 0.7; // Incase 2 disconnects
        boolean shouldIntake = gamepad1.right_bumper || gamepad2.right_bumper;
        boolean shouldReverseTransfer = gamepad2.cross;
        boolean shouldReverseIntake = gamepad2.square;
        boolean shouldTransfer = gamepad2.circle || gamepad1.circle;
        boolean shouldToggleDebug = gamepad1.optionsWasPressed() || gamepad2.optionsWasPressed();

        robot.follower().manual(forward, lateral, turn);

        if(shouldStaticVelocity) {
            robot.shooter().setVelocity(Shooter.TESTING_SHOOTER_VELO);
        } else if (shouldDynamicVelocity){
            robot.shooter().setVelocity(
                    robot.shooterVelocityFor(Globals.redBasketAudienceGoal)
            );
        }else {
            robot.shooter().stop();
        }

        if(shouldIntake) {
            robot.intake().intake();
        } else if(shouldReverseIntake){
            robot.intake().outtake();
        } else {
            robot.intake().stop();
        }

        if(shouldTransfer) {
            robot.transfer().transfer();
        } else if(shouldReverseTransfer) {
            robot.transfer().outtake();
        } else {
            robot.transfer().stop();
        }

        if(shouldToggleDebug) showDebugInfo = !showDebugInfo;

        // telemetryM / LOOP

        if(showDebugInfo) {
            robot.showDebugInfoOn(telemetryM);
            telemetryM.addData("Goal Distance", robot.currentPose().distance(Globals.redBasketAudienceGoal));
            telemetryM.addLine("");
        }
        telemetryM.addLine("G1 OR G2 / OPTIONS : " + (showDebugInfo ? "Hide" : "Show") + " Debug Information");
        telemetryM.addLine("G1 OR G2 / RIGHT BUMPER : Intake");
        telemetryM.addLine("G2 / CROSS : Outtake");
        telemetryM.addLine("G1 OR G2 / CIRCLE : Transfer");
        telemetryM.addLine("G2 / SQUARE : Reverse Transfer");
        telemetryM.addLine("G1 OR G2 / RIGHT TRIGGER : Dynamic Shooter Velocity");
        telemetryM.addLine("G1 OR G2 / LEFT TRIGGER : Static Shooter Velocity");

        robot.drawRobotOnPanels();
        robot.loop();
        telemetryM.update(telemetry);
    }

}
