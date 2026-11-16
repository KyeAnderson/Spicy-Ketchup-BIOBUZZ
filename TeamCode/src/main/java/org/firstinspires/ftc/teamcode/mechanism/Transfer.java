package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImpl;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.helpers.HardwareConstants;

public class Transfer extends Mechanism {
    public enum TransferVelocity {
        OFF(0, 0),
        FRONT_ON(1500, 0),
        OUTTAKE(-1500, -1),
//        ON(1500, 1);
        ON(750, 1);
        final int motorVelocity;
        final double servoPower;
        TransferVelocity(int i, double c) {
            motorVelocity = i;
            servoPower = c;
        }

        public double getServoPower() {
            return servoPower;
        }

        public int getMotorVelocity() {
            return motorVelocity;
        }
    }

    TransferVelocity setpointVelocity;
    DcMotorEx motor;
    CRServo servo;
    double toRPM(double tps) {
        double ENCODER_PPR = 145.1; // https://www.gobilda.com/5203-series-yellow-jacket-planetary-gear-motor-5-2-1-ratio-24mm-length-8mm-rex-shaft-1150-rpm-3-3-5v-encoder
        return ((tps/ ENCODER_PPR) * 60);
    }
    public Transfer(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        motor = hm.get(DcMotorEx.class, HardwareConstants.TRANSFER_MOTOR.getId());
        servo = hm.get(CRServo.class, HardwareConstants.TRANSFER_SERVO.getId());
        setpointVelocity = TransferVelocity.OFF;
        displayTelemetry("RPM", ()->toRPM(motor.getVelocity()));
        displayTelemetry("Target RPM", ()-> toRPM(setpointVelocity.getMotorVelocity()));
        displayTelemetry("Current Servo Power", servo::getPower);
        displayTelemetry("Target Servo Power", setpointVelocity::getServoPower);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        initializeMotors(motor);
    }

    public void setVelocity(TransferVelocity velo) {
        if(setpointVelocity == velo) return;
        setpointVelocity = velo;
    }

    public void runFrontOnly() {
        setVelocity(TransferVelocity.FRONT_ON);
    }
    public void outtake() {
        setVelocity(TransferVelocity.OUTTAKE);
    }
    public void transfer() {
        setVelocity(TransferVelocity.ON);
    }

    public void stop() {
        setVelocity(TransferVelocity.OFF);
    }

    @Override
    public void loop() {
        motor.setVelocity(setpointVelocity.getMotorVelocity());
        servo.setPower(setpointVelocity.getServoPower());
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        telemetryM.addLine("G1 / Square: set velocity to TransferVelocity.ON");
        telemetryM.addLine("G1 / Triangle: set velocity to TransferVelocity.FRONT_ONLY");
        telemetryM.addLine("G1 / Circle: set velocity to TransferVelocity.OFF");
        if(g1.squareWasPressed()) {
            transfer();
        }
        if(g1.triangleWasPressed()) {
            runFrontOnly();
        }
        if(g1.circleWasPressed()) {
            stop();
        }

    }
}
