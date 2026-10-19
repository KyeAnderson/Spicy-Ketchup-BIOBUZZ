package org.firstinspires.ftc.teamcode.helpers;

public enum IntakeVelocity {
    OFF(0),
    ON(5000),
    SLOW(500);
    final int power;
    IntakeVelocity(int i) {
        power = i;
    }

    public int getPower() {
        return power;
    }
}