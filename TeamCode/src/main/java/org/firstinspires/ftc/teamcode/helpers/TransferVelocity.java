package org.firstinspires.ftc.teamcode.helpers;

public enum TransferVelocity {
    OFF(0, 0),
    FRONT_ON(1500, 0),
    ON(1500, 1);
    final int power;
    final double cPower;
    TransferVelocity(int i, double c) {
        power = i;
        cPower = c;
    }

    public double getCPower() {
        return cPower;
    }

    public int getPower() {
        return power;
    }
}
