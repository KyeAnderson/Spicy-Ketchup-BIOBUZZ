package org.firstinspires.ftc.teamcode.commandeer;

import java.util.LinkedList;

public class CQueue {
    final LinkedList<CCommand> bufferQueue;
    LinkedList<CCommand> commandQueue;
    public CQueue() {
        commandQueue = new LinkedList<>();
        bufferQueue = new LinkedList<>();
    }
    
    public CQueue push(CCommand command) {
        synchronized (bufferQueue) {
            bufferQueue.add(command);
        }
        return this;
    }

    public void prioritize(CCommand command) {
        commandQueue.addFirst(command);
    }

    public void update() {
        commandQueue.addAll(bufferQueue);
        bufferQueue.clear();
        for (int i = 0; i < commandQueue.size(); i++) {
            CCommand cmd = commandQueue.get(i);
            updateCommand(cmd);
            if (cmd.getState() == CState.END || cmd.getState() == CState.ERROR) {
                i--;
            }
        }
    }
    
    public boolean empty() {
        return commandQueue.isEmpty();
    }

    @Override
    public String toString() {
        return commandQueue.toString();
    }

    private void updateCommand(CCommand currentCommand) {
        if (currentCommand.getState() == CState.IDLE) {
            currentCommand.start();
        }
        currentCommand.update();
        if (currentCommand.getState() == CState.END || currentCommand.getState() == CState.ERROR) {
            commandQueue.remove(currentCommand);
            System.out.println("queue updated, size: " + commandQueue.size());
        }
    }
}
