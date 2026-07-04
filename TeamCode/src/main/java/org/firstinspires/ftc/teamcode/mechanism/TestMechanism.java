package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.helpers.HardwareConstants;

public class TestMechanism extends Mechanism {
    Servo servo;

    public TestMechanism(HardwareMap hm, Telemetry tele) {
        super(hm, tele);

        servo = hm.get(Servo.class, HardwareConstants.TEST_SERVO.getId());
    }

    @Override
    public void loop() {

    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {

    }
}
