package org.firstinspires.ftc.teamcode.helpers;

public enum HoodCompression  {
    POLLEN(0.6, 1200),
    NECTAR(0.45, 1200);
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