package org.firstinspires.ftc.teamcode.helpers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public abstract class SpicyMode extends OpMode {
    public MatchSide side;
    public SpicyMode(MatchSide side) {
        this.side = side;
    }

    public void init() {
        if(side == MatchSide.RED) onRedInit();
        else onBlueInit();
    }

    public abstract void onRedInit();
    public abstract void onBlueInit();
    public abstract String getOpModeName();
}
