package org.firstinspires.ftc.teamcode.opmode;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.helpers.SpicyMode;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

public class SpicyTele extends SpicyMode {
    public SpicyTele(Globals.MatchSide side) {super(side);}
    Follower f;

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

        f = Constants.createFollower(hardwareMap);

        telemetry.addData("We're on", side.name());
        telemetry.update();
    }

    @Override
    public void start() {
        f.startTeleOpDrive(true);
    }

    @Override
    public void loop() {
        f.update();

        f.setTeleOpDrive(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                -gamepad1.right_stick_x,
                true
        );

        Globals.lastPose = f.getPose();
    }

    @Override
    public String getOpModeName() {
        return "Colton at League Meet 1: %s";
    }
}
