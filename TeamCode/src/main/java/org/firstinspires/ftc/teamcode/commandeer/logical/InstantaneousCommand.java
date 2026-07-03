package org.firstinspires.ftc.teamcode.commandeer.logical;

import org.firstinspires.ftc.teamcode.commandeer.CCommand;
import org.firstinspires.ftc.teamcode.commandeer.CState;

public class InstantaneousCommand extends CCommand {
    public InstantaneousCommand(Object... args) {
        super(()->CState.ERROR, args);
    }

    public static InstantaneousCommand with(Runnable update) {
        InstantaneousCommand me = builder();
        me.setCommand(() -> {
            if (me.getState() != CState.SKIPPABLE) {
                update.run();
            }
            return CState.SKIPPABLE;
        });
        return me;
    }

    public static InstantaneousCommand builder() {
        return new InstantaneousCommand();
    }
}
