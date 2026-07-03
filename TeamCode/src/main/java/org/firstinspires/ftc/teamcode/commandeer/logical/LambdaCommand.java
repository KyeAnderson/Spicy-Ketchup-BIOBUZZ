package org.firstinspires.ftc.teamcode.commandeer.logical;

import org.firstinspires.ftc.teamcode.commandeer.CCommand;
import org.firstinspires.ftc.teamcode.commandeer.CState;

import java.util.function.Supplier;

public class LambdaCommand extends CCommand {
    Supplier<CState> updateFunction;
    
    public LambdaCommand(Object... args) {
        super(()->CState.ERROR, args);
    }

    public static LambdaCommand with(Supplier<CState> update) {
        return builder().setUpdateFunction(update).build();
    }
    
    public LambdaCommand setUpdateFunction(Supplier<CState> updateFunction) {
        this.updateFunction = updateFunction;
        return this;
    }
    
    public LambdaCommand build() {
        this.setCommand(()-> updateFunction.get());
        return this;
    }

    public static LambdaCommand builder() {
        return new LambdaCommand();
    }
}
