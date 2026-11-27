package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.helpers.HardwareConstants;

public class Flowermabob extends Mechanism {
    private Robot robot;
    public enum FlowermabobMode {
        RETRACT(0.41),
        EXTEND(0.20);
        final double position;
        FlowermabobMode(double i) {
            position = i;
        }

        public double getPosition() {
            return position;
        }
    }

    Servo servoLeft;
    Servo servoRight;
    FlowermabobMode mode;

    public Flowermabob(HardwareMap hm, Telemetry tele, Robot robot) {
        super(hm, tele);
        this.robot = robot;
        servoLeft = hm.get(Servo.class, HardwareConstants.FLOWER_SERVO_L.getId());
        servoRight = hm.get(Servo.class, HardwareConstants.FLOWER_SERVO_R.getId());
        servoLeft.setDirection(Servo.Direction.REVERSE);
        mode = FlowermabobMode.RETRACT;
        displayTelemetry("Setpoint Pos", mode::getPosition);
        displayTelemetry("Servo L Pos", ()->servoLeft.getPosition());
        displayTelemetry("Servo R Pos", ()->servoRight.getPosition());
    }

    public void setMode(FlowermabobMode nextVelocity) {
        if(mode == nextVelocity) return;
        mode = nextVelocity;
    }

    public void retract() {
        setMode(FlowermabobMode.RETRACT);
    }
    public void extend() {
        setMode(FlowermabobMode.EXTEND);
    }

    @Override
    public void loop() {
        servoLeft.setPosition(mode.getPosition());
        servoRight.setPosition(mode.getPosition());
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        if(g1.circleWasPressed()) retract();
        if(g1.crossWasPressed()) extend();

        telemetryM.addLine("G1 / Circle : Retract");
        telemetryM.addLine("G1 / Cross : Extend");
    }
}
