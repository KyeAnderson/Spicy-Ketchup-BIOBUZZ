package org.firstinspires.ftc.teamcode.commandeer.logical;

import org.firstinspires.ftc.teamcode.commandeer.CCommand;
import org.firstinspires.ftc.teamcode.commandeer.CState;

import java.util.function.Supplier;

public class Loop extends CCommand {
    Supplier<CState> updateFunction;
    
    public Loop(Object... args) {
        super(()->CState.ERROR, args);
    }

    public static Loop using(CCommand cmd) {
        return builder().setUpdateFunction(()->{
            cmd.update();
            return CState.SKIPPABLE;
        }).build();
    }
    
    public Loop setUpdateFunction(Supplier<CState> updateFunction) {
        this.updateFunction = updateFunction;
        return this;
    }
    
    public Loop build() {
        this.setCommand(()-> updateFunction.get());
        return this;
    }

    public static Loop builder() {
        return new Loop();
    }
}
