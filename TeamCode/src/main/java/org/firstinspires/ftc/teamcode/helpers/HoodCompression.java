package org.firstinspires.ftc.teamcode.helpers;

public enum HoodCompression  {
    POLLEN(0.6),
    NECTAR(0.45);
    final double pos;
    HoodCompression(double i) {
        pos = i;
    }

    public double getPosition() {
        return pos;
    }
}