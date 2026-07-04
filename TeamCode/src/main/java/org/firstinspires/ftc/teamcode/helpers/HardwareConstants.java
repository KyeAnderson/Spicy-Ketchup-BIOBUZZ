package org.firstinspires.ftc.teamcode.helpers;

public enum HardwareConstants {
    TEST_SERVO("cr"),
    TEST_MOTOR("mtr");

    private final String id;
    HardwareConstants(String id) {
        this.id = id;
    }
    public String getId() {return id;}
}
