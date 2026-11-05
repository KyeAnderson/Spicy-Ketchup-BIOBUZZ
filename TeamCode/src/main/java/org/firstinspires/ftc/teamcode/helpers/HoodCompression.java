package org.firstinspires.ftc.teamcode.helpers;

public enum HoodCompression  {
    POLLEN(0.6, 2400), // Changed from 1200 -> 2400. Only 2 pollen would shoot
    NECTAR(0.45, 2400);// Changed from 1200 -> 2400. Only 2 pollen would shoot
    final double pos;
    final double time;
    HoodCompression(double i, double msShootTime) {
        pos = i;
        time = msShootTime;
    }

    public double getPosition() {
        return pos;
    }

    public double getTime() {
        return time;
    }
}