package org.firstinspires.ftc.teamcode.commandeer.logical;

import com.pedropathing.util.Timer;
import org.firstinspires.ftc.teamcode.commandeer.CCommand;
import org.firstinspires.ftc.teamcode.commandeer.CState;

public class Wait extends CCommand {
    double ms;
    Timer timer;
    public Wait(double ms) {
        super(()->CState.WORKING, ms);
        this.ms = ms;
        timer = new Timer();
    }

    @Override
    public void start() {
        super.start();
        timer.resetTimer();
    }
    
    public static Wait forMs(double ms) {return new Wait(ms).build();}

    public Wait build() {
        this.setCommand(() -> {
            if(timer.getElapsedTime() >= ms) return CState.END;
            return CState.WORKING;
        });
        return this;
    }
}