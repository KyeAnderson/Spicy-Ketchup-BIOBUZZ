package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.helpers.HardwareConstants;

public class Turret extends Mechanism {
    Servo s1;
    Servo s2;
    public Turret(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        s1 = hm.get(Servo.class, HardwareConstants.TURRET_SERVO_L.getId());
        s2 = hm.get(Servo.class, HardwareConstants.TURRET_SERVO_R.getId());
    }

    @Override
    public void loop() {

    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {

    }
}
