package org.firstinspires.ftc.teamcode.opmode.auto;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.helpers.SpicyMode;

public class AutoUno extends SpicyMode {
    enum AutoState {
        INIT,
        MOVE,
        END
    }
    AutoState state = AutoState.INIT;

    public AutoUno(Globals.MatchSide side) {
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
        switch(state) {
            case INIT:
                setState(AutoState.MOVE);
                break;
            case MOVE:
                setState(AutoState.END);
                break;
            case END:
                break;
        }
    }

    void setState(AutoState state) {
        this.state = state;
    }

    @Override
    public String getOpModeName() {
        return "AUTO 1... %s";
    }
}
