package org.firstinspires.ftc.teamcode.helpers;

public enum HardwareConstants {
    INTAKE_MOTOR("spin"),
    TRANSFER_MOTOR("tf"),
    TRANSFER_SERVO("ts"),
    TURRET_MOTOR("tm"),
    FLOWER_SERVO_L("fs1"),
    FLOWER_SERVO_R("fs2"),
    SHOOTER_FLYWHEEL_MOTOR("fly"),
    SHOOTER_COMPRESSION_SERVO("ang"),
    LIMELIGHT("limelight");
    //TEST_MOTOR("mtr");

    private final String id;
    HardwareConstants(String id) {
        this.id = id;
    }
    public String getId() {return id;}
}
