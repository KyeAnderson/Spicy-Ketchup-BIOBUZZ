package org.firstinspires.ftc.teamcode.opmode;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.helpers.HoodCompression;
import org.firstinspires.ftc.teamcode.helpers.SpicyMode;
import org.firstinspires.ftc.teamcode.mechanism.Shooter;
import org.firstinspires.ftc.teamcode.mechanism.Spintake;
import org.firstinspires.ftc.teamcode.mechanism.Transfer;
import org.firstinspires.ftc.teamcode.pedro.Constants;

public class SpicyTele extends SpicyMode {
    public SpicyTele(Globals.MatchSide side) {super(side, Globals.ModeType.TELE);}
    private Follower f;
    private Spintake spintake;
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
        spintake = new Spintake(hardwareMap, telemetry);
        shooter = new Shooter(hardwareMap, telemetry);
        transfer = new Transfer(hardwareMap, telemetry);

        telemetry.addData("We're on", side.name());
        telemetry.update();
    }


    @Override
    public void loop() {
        Globals.lastPose = f.pose();
        telemetry.addLine(spintake.toString());
        telemetry.addLine(shooter.toString());

        double forward = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double turn = -gamepad1.right_stick_x;

        f.manual(forward,lateral,turn);

        if(gamepad1.right_trigger > 0.7) {
            shooter.setVelocity(1870);
            if(shooter.flywheelAtTarget()) {
                transfer.setVelocity(Transfer.TransferVelocity.ON);
            } else {
                transfer.setVelocity(Transfer.TransferVelocity.OFF);
            }
        } else {
            shooter.setVelocity(0);
            transfer.setVelocity(Transfer.TransferVelocity.OFF);
        }
        if(gamepad1.right_bumper) {
            spintake.setVelocity(Spintake.SpintakeVelocity.ON);
        } else {
            spintake.setVelocity(Spintake.SpintakeVelocity.OFF);
        }

        if(gamepad1.squareWasPressed()) {
            shooter.setCompression(HoodCompression.POLLEN);
        }
        if(gamepad1.circleWasPressed()) {
            shooter.setCompression(HoodCompression.NECTAR);
        }

        spintake.loop();
        shooter.loop();
        f.update();
        telemetry.update();
    }

    @Override
    public String getOpModeName() {
        return "Colton at League Meet 1: %s";
    }
}
