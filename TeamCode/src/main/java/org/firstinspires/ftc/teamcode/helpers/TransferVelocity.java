package org.firstinspires.ftc.teamcode.helpers;

public enum TransferVelocity {
    OFF(0),
    ON(1500);
    final int power;
    TransferVelocity(int i) {
        power = i;
    }

    public int getPower() {
        return power;
    }
}
