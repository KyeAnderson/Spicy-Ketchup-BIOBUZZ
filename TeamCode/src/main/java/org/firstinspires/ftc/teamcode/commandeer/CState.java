package org.firstinspires.ftc.teamcode.commandeer;

public enum CState {
    IDLE, // ready to commit
    BEGIN, // start
    WORKING, // in progress, block
    SKIPPABLE, // in progress, but no need to block
    PAUSED, // paused by user or system
    END, // intermediate
    ERROR // err
}
