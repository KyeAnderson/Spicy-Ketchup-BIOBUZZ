package org.firstinspires.ftc.teamcode.opmode;

import org.firstinspires.ftc.teamcode.helpers.MatchSide;
import org.firstinspires.ftc.teamcode.helpers.SpicyMode;

public class SpicyTele extends SpicyMode {
    public SpicyTele(MatchSide side) {
        super(side);
    }

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

        telemetry.addData("We're on", side.name());
        telemetry.update();
    }

    @Override
    public void loop() {

    }

    @Override
    public String getOpModeName() {
        return "Colton at League Meet 1: %s";
    }
}
