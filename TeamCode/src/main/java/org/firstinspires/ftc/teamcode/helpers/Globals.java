package org.firstinspires.ftc.teamcode.helpers;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.teamcode.commandeer.test.ExampleCommandeerTest;
import org.firstinspires.ftc.teamcode.opmode.SpicyTele;
import org.firstinspires.ftc.teamcode.opmode.auto.AutoUno;

import java.util.HashSet;
import java.util.Set;

public class Globals {
    public static Pose lastPose;
    public static final boolean hideTests = false;
    static Set<SpicyMode> modes;

    public enum MatchSide {
        RED,
        BLUE,
    }


    ///  ====== OPMODE REGISTRATION
    /// TODO: Add OpModes here
    private static Set<SpicyMode> buildOpModes() {
        modes = new HashSet<>();
        modes.add(new ExampleCommandeerTest(Globals.MatchSide.RED));

        modes.add(new SpicyTele(Globals.MatchSide.RED));
        modes.add(new SpicyTele(Globals.MatchSide.BLUE));

        modes.add(new AutoUno(Globals.MatchSide.RED));
        modes.add(new AutoUno(Globals.MatchSide.BLUE));

        return modes;
    }

    @OpModeRegistrar
    public static void register(OpModeManager manager) {
        for(SpicyMode mode : buildOpModes()) {
            manager.register(metaForMode(mode), mode);
        }
    }

    public static OpModeMeta metaForMode(SpicyMode mode) {
        String name = mode.getOpModeName();
        if(mode.getOpModeName().contains("%s")) { // able to add the side's name
            name = String.format(mode.getOpModeName(), mode.side.name());
        }
        return new OpModeMeta.Builder()
                .setName(name)
                .setFlavor(OpModeMeta.Flavor.TELEOP)
                .setSource(OpModeMeta.Source.BLOCKLY)
                .build();
    }
}
