package org.firstinspires.ftc.teamcode.commandeer.logical;

import com.pedropathing.util.Timer;
import org.firstinspires.ftc.teamcode.commandeer.CCommand;
import org.firstinspires.ftc.teamcode.commandeer.CState;

import java.util.LinkedHashMap;

public class SimultaneousGroup extends CCommand {
    LinkedHashMap<CCommand, CState> activeCmds;
    Timer timer;
    double msTimeout = 0;
    public SimultaneousGroup(Object... args) {
        super(()->CState.ERROR, args);
        this.activeCmds = new LinkedHashMap<>();
        this.timer = new Timer();
    }

    public SimultaneousGroup alongside(CCommand command) {
        activeCmds.put(command, command.getState());
        return this;
    }
    public SimultaneousGroup holdFor(double ms) {
        this.msTimeout = ms;
        return this;
    }

    @Override
    public void start() {
        super.start();
        timer.resetTimer();
    }

    public SimultaneousGroup build() {
        this.setCommand(() -> {
            boolean allDone = true;
            for (CCommand currentCommand : activeCmds.keySet()) {
                if (currentCommand.getState() == CState.IDLE) {
                    currentCommand.start();
                }
                currentCommand.update();
                if(currentCommand.getState() != CState.SKIPPABLE) {
                    allDone = false;
                }
            }
            
            activeCmds.entrySet().removeIf(entry ->
                    entry.getKey().getState() == CState.END ||
                            entry.getKey().getState() == CState.ERROR
            );

            return allDone && (timer.getElapsedTime() > this.msTimeout) ? CState.END : CState.WORKING;
        });
        return this;
    }

    @Override
    public String toString() {
        return this.msTimeout +"ms:"+ activeCmds.toString();
    }

    public static SimultaneousGroup builder() {
        return new SimultaneousGroup();
    }
}
