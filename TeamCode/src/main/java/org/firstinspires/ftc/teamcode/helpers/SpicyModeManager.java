package org.firstinspires.ftc.teamcode.helpers;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.teamcode.opmode.SpicyTele;
import org.firstinspires.ftc.teamcode.opmode.auto.AutoUno;

import java.util.HashSet;
import java.util.Set;

public class SpicyModeManager {
    static Set<SpicyMode> modes;

    private static Set<SpicyMode> buildOpModes() {
        modes = new HashSet<>();
        modes.add(new SpicyTele(MatchSide.RED));
        modes.add(new SpicyTele(MatchSide.BLUE));

        modes.add(new AutoUno(MatchSide.RED));
        modes.add(new AutoUno(MatchSide.BLUE));

        // TODO: Add more OpModes
        return modes;
    }

    @OpModeRegistrar
    public static void register(OpModeManager manager) {
        for(SpicyMode mode : buildOpModes()) {
            manager.register(metaForMode(mode), mode);
        }
    }

    public static OpModeMeta metaForMode(SpicyMode mode) {
        return new OpModeMeta.Builder()
                .setName(String.format(mode.getOpModeName(), mode.side.name()))
                .setFlavor(OpModeMeta.Flavor.TELEOP)
                .setSource(OpModeMeta.Source.BLOCKLY)
                .build();
    }
}
