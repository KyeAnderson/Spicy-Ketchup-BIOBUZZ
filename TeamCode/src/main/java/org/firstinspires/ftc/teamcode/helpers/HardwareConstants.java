package org.firstinspires.ftc.teamcode.helpers;

public enum HardwareConstants {
    INTAKE_MOTOR("spin"),
    TRANSFER_MOTOR("tf"),
    TRANSFER_SERVO("ts"),
    TURRET_SERVO_L("ts1"),
    TURRET_SERVO_R("ts2"),
    SHOOTER_FLYWHEEL_MOTOR("fly"),
    SHOOTER_COMPRESSION_SERVO("ang"),

    TEST_MOTOR("mtr");

    private final String id;
    HardwareConstants(String id) {
        this.id = id;
    }
    public String getId() {return id;}
}
