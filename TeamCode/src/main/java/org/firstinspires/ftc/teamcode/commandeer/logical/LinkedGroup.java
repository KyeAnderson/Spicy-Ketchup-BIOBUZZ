package org.firstinspires.ftc.teamcode.commandeer.logical;

import org.firstinspires.ftc.teamcode.commandeer.CCommand;
import org.firstinspires.ftc.teamcode.commandeer.CState;

import java.util.LinkedList;

public class LinkedGroup extends CCommand {
    LinkedList<CCommand> cmds;
    public LinkedGroup(Object... args) {
        this.cmds = new LinkedList<>();
        super(()->CState.ERROR, args);
    }

    public LinkedGroup add(CCommand command) {
        cmds.add(command);
        return this;
    }
    
    public LinkedGroup build() {
        this.setCommand(()-> {
            if(!cmds.isEmpty()) {
                CCommand currentCommand = cmds.peekFirst();
                if (currentCommand.getState() == CState.IDLE) {
                    currentCommand.start();
                }
                currentCommand.update();
                if (currentCommand.getState() == CState.END || currentCommand.getState() == CState.ERROR) {
                    cmds.pollFirst();
                    return CState.WORKING;
                } else {
                    return cmds.getFirst().getState();
                }
            }
            return CState.END;
        });
        return this;
    }

    @Override
    public String toString() {
        return cmds.toString();
    }

    public static LinkedGroup builder() {
        return new LinkedGroup();
    }
}
