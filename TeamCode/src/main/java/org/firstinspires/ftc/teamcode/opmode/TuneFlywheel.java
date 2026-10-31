package org.firstinspires.ftc.teamcode.opmode;

import androidx.collection.ArraySet;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.helpers.HoodCompression;
import org.firstinspires.ftc.teamcode.helpers.IntakeVelocity;
import org.firstinspires.ftc.teamcode.helpers.SpicyMode;
import org.firstinspires.ftc.teamcode.helpers.TransferVelocity;
import org.firstinspires.ftc.teamcode.mechanism.Intake;
import org.firstinspires.ftc.teamcode.mechanism.Shooter;
import org.firstinspires.ftc.teamcode.mechanism.Transfer;
import org.firstinspires.ftc.teamcode.pedro.Constants;

public class TuneFlywheel extends SpicyMode {
    public TuneFlywheel(Globals.MatchSide side) {super(side, Globals.ModeType.TELE);}
    private Follower f;
    private Intake intake;
    private Shooter shooter;
    private Transfer transfer;
    ArraySet<double[]> doubleList;

    @Override
    public void onRedInit() {
        telemetry.addLine("This will only show up on RED.");
    }

    @Override
    public void onBlueInit() {
        telemetry.addLine("This will only show up on BLUE.");
    }

    float rpm = 1000;
    float add = 100;

    @Override
    public void init() {
        super.init();
        doubleList = new ArraySet<>();

        f = Constants.create(hardwareMap);
        f.setPose(new Pose(11, 10.5, Math.toRadians(0)));
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
            if(shooter.flywheelAtTarget()) transfer.setVelocity(TransferVelocity.ON);
            else transfer.stop();
        } else {
            transfer.stop();
        }

        if(shouldIntake) {
            intake.setVelocity(IntakeVelocity.ON);
            transfer.setVelocity(TransferVelocity.FRONT_ON);
        } else if(gamepad1.left_bumper) {
//            intake.setVelocity(IntakeVelocity.OUTTAKE);
            transfer.setVelocity(TransferVelocity.OUTTAKE);
        } else {
            intake.stop();
            if(!triggerPressed) transfer.stop();
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
            doubleList.add(new double[] {Globals.redBasketAudienceGoal.distance(f.pose()), rpm});
        }
        shooter.setVelocity(rpm);

        // TELEMETRY / LOOP

        telemetry.addData("Edit Size", add);
        telemetry.addData("d", f.pose().distance(Globals.redBasketAudienceGoal));
        telemetry.addData("RPM", rpm);
        telemetry.addData("CRV", shooter.getVelocity());
        telemetry.addLine("G1 Dpad LEFT : -50RPM from size");
        telemetry.addLine("G1 Dpad RIGHT : +50RPM from size");
        telemetry.addLine("G1 Dpad UP : +" + add+"RPM");
        telemetry.addLine("G1 Dpad DOWN : -" + add +"RPM");
        for(double[] i : doubleList) {
            telemetry.addLine("{" + i[0] + ", " + i[1]+"},");
        }
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
        return "tune flyswhelss %s";
    }
}
