package org.firstinspires.ftc.teamcode.helpers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public abstract class SpicyMode extends OpMode {
    public Globals.MatchSide side;
    public SpicyMode(Globals.MatchSide side) {
        this.side = side;
    }

    public void init() {
        if(side == Globals.MatchSide.RED) onRedInit();
        else onBlueInit();
    }

    /**
     * What init code [i.e. path variables set] to run when the Red side SpicyMode is selected.
     */
    public void onRedInit() {};

    /**
     * What init code [i.e. path variables set] to run when the Blue side SpicyMode is selected.
     */
    public void onBlueInit() {};

    /**
     * Define how the OpMode should be named.
     * %s represents where to place the side name.
     * Example: (OpMode %s) -> (OpMode BLUE) and (OpMode RED)
     */
    public abstract String getOpModeName();
}
