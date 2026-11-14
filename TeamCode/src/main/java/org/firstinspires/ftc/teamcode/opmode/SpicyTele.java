package org.firstinspires.ftc.teamcode.opmode;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.mechanism.Shooter;
import org.firstinspires.ftc.teamcode.mechanism.Intake;
import org.firstinspires.ftc.teamcode.mechanism.Transfer;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.Drawing;

@TeleOp(name="Powell at League Meet 2: Hola")
public class SpicyTele extends OpMode {
    private Follower f;
    private Intake intake;
    private Shooter shooter;
    private Transfer transfer;

    @Override
    public void init() {
        f = Constants.create(hardwareMap);
        f.setPose(new Pose(58,10,0));
        intake = new Intake(hardwareMap, telemetry);
        shooter = new Shooter(hardwareMap, telemetry);
        transfer = new Transfer(hardwareMap, telemetry);

        telemetry.update();
    }

    @Override
    public void loop() {
        /// DRIVE

        Globals.lastPose = f.pose();

        double forward = -gamepad1.left_stick_y;
        double lateral = -gamepad1.left_stick_x;
        double turn = -gamepad1.right_stick_x;

        f.manual(forward,lateral,turn);
        /// CONTROLS

        boolean rTriggerPressed = gamepad2.right_trigger > 0.7 || gamepad1.right_trigger > 0.7; // Incase 2 disconnects
        boolean lTriggerPressed = gamepad2.left_trigger > 0.7 || gamepad1.left_trigger > 0.7; // Incase 2 disconnects

        boolean shouldIntake = gamepad1.right_bumper || gamepad2.right_bumper;
        boolean shouldReverseTransfer = gamepad2.cross;
        boolean shouldReverseIntake = gamepad2.square;
        boolean shouldTransfer = gamepad2.circle || gamepad1.circle;


        if(lTriggerPressed) {
            shooter.setVelocity(Shooter.TESTING_SHOOTER_VELO);
        } else if (rTriggerPressed){
            shooter.setVelocity(shooter.getVelocityForDist(f.pose().distance(Globals.redBasketAudienceGoal)));
        }else {
            shooter.stop();
        }

        if(shouldIntake) {
            intake.intake();
        } else if(shouldReverseIntake){
            intake.outtake();
        } else {
            intake.stop();
        }

        if(shouldTransfer) {
            transfer.transfer();
        } else if(shouldReverseTransfer) {
            transfer.outtake();
        } else {
            transfer.stop();
        }

        // TELEMETRY / LOOP

        telemetry.addLine("Gamepad1 Right Trigger: Shooter (set velocity to 1650 tps)");
        telemetry.addLine("Gamepad1 Right Bumper: Intake (hold)");
        telemetry.addLine("Gamepad1 Square: Set Hood Compression for Pollen");
        telemetry.addLine("Gamepad1 Circle: Set Hood Compression for Nectar");
        telemetry.addLine(intake.toString());
        telemetry.addLine(shooter.toString());
        telemetry.addLine(transfer.toString());
        telemetry.addData("Pose", f.pose().toString());
        telemetry.addData("Pose", f.pose().distance(Globals.redBasketAudienceGoal));


        Drawing.drawCurrent(f);
        intake.loop();
        shooter.loop();
        f.update();
        transfer.loop();
        telemetry.update();
    }

}
