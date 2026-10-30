package org.firstinspires.ftc.teamcode.opmode;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.helpers.HoodCompression;
import org.firstinspires.ftc.teamcode.helpers.IntakeVelocity;
import org.firstinspires.ftc.teamcode.helpers.SpicyMode;
import org.firstinspires.ftc.teamcode.helpers.TransferVelocity;
import org.firstinspires.ftc.teamcode.mechanism.Shooter;
import org.firstinspires.ftc.teamcode.mechanism.Intake;
import org.firstinspires.ftc.teamcode.mechanism.Transfer;
import org.firstinspires.ftc.teamcode.pedro.Constants;

public class SpicyTele extends SpicyMode {
    public SpicyTele(Globals.MatchSide side) {super(side, Globals.ModeType.TELE);}
    private Follower f;
    private Intake intake;
    private Shooter shooter;
    private Transfer transfer;

    @Override
    public void onRedInit() {
        telemetry.addLine("This will only show up on RED.");
    }

    @Override
    public void onBlueInit() {
        telemetry.addLine("This will only show up on BLUE.");
    }

    @Override
    public void init() {
        super.init();

        f = Constants.create(hardwareMap);
        intake = new Intake(hardwareMap, telemetry);
        shooter = new Shooter(hardwareMap, telemetry);
        transfer = new Transfer(hardwareMap, telemetry);

        telemetry.addData("We're on", side.name());
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

        boolean triggerPressed = gamepad1.right_trigger > 0.7;
        boolean shoulderPressed = gamepad1.right_bumper;
        boolean shouldIntake = shoulderPressed && !triggerPressed;

        if(triggerPressed) {
            shooter.setVelocity(Shooter.TESTING_SHOOTER_VELO);
            if(shooter.flywheelAtTarget()) transfer.setVelocity(TransferVelocity.ON);
            else transfer.stop();
        } else {
            shooter.stop();
        }

        if(shouldIntake) {
            intake.setVelocity(IntakeVelocity.ON);
            transfer.setVelocity(TransferVelocity.FRONT_ON);
        } else {
            intake.stop();
            if(!triggerPressed) transfer.stop();
        }


        if(gamepad1.triangle) {
            if(!f.holding()) f.hold(f.pose());
        } else {

        }

        if(gamepad1.squareWasPressed()) {
            shooter.setCompression(HoodCompression.POLLEN);
        }
        if(gamepad1.circleWasPressed()) {
            shooter.setCompression(HoodCompression.NECTAR);
        }

        // TELEMETRY / LOOP

        telemetry.addLine("Gamepad1 Right Trigger: Shooter (set velocity to 1870 tps)");
        telemetry.addLine("Gamepad1 Right Bumper: Intake (hold)");
        telemetry.addLine("Gamepad1 Square: Set Hood Compression for Pollen");
        telemetry.addLine("Gamepad1 Circle: Set Hood Compression for Nectar");
        telemetry.addLine(intake.toString());
        telemetry.addLine(shooter.toString());
        telemetry.addLine(transfer.toString());

        intake.loop();
        shooter.loop();
        f.update();
        transfer.loop();
        telemetry.update();
    }

    @Override
    public String getOpModeName() {
        return "Colton at League Meet 1: %s";
    }
}
