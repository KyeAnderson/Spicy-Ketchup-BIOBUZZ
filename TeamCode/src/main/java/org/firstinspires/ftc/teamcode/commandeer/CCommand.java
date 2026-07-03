package org.firstinspires.ftc.teamcode.commandeer;

import java.util.LinkedList;
import java.util.function.Supplier;

public class CCommand {
    CState state;
    private final Object[] args;
    public CState getState() {
        return state;
    }
    protected void setCommand(Supplier<CState> periodic) {
        command = periodic;
    }
    Supplier<CState> command;
    
    public CCommand(Supplier<CState> periodic, Object... args) {
        this.command = periodic;
        this.state = CState.IDLE;
        this.args = args;
    }
    
    public void start() {
        if (state == CState.IDLE) {
            state = CState.BEGIN;
        }
    }
    
    public void update() {
        if (state == CState.BEGIN || state == CState.WORKING) {
            state = command.get();
        }
    }
    
    public void prioritize(CQueue queue) {
        queue.prioritize(this);
    }
    public void queue(LinkedList<CCommand> queue) {
        queue.add(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +": " + state;
    }

    public Object[] getArgs() {
        return args;
    }
    
}
