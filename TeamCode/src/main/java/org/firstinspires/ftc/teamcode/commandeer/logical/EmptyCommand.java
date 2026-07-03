package org.firstinspires.ftc.teamcode.commandeer.logical;

import org.firstinspires.ftc.teamcode.commandeer.CCommand;
import org.firstinspires.ftc.teamcode.commandeer.CState;

import java.util.function.Supplier;

public class EmptyCommand extends CCommand {
    Supplier<CState> updateFunction;
    
    public EmptyCommand(Object... args) {
        super(()->CState.ERROR, args);
    }

    public EmptyCommand setUpdateFunction(Supplier<CState> updateFunction) {
        this.updateFunction = updateFunction;
        return this;
    }
    
    public EmptyCommand build() {
        this.setCommand(()-> updateFunction.get());
        return this;
    }

    public static EmptyCommand builder() {
        return new EmptyCommand();
    }
}
