package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.helpers.HardwareConstants;

public class Hood extends Mechanism{
    public enum HoodCompression  {
        POLLEN(0.6, 2400), // Changed from 1200 -> 2400. Only 2 pollen would shoot
        NECTAR(0.45, 2400);// Changed from 1200 -> 2400. Only 2 pollen would shoot
        final double servoPosition;
        final double timeToScore;
        HoodCompression(double servoPos, double msShootTime) {
            servoPosition = servoPos;
            timeToScore = msShootTime;
        }

        public double getPosition() {
            return servoPosition;
        }

        public double getTimeToScore() {
            return timeToScore;
        }
    }

    Servo compressor;
    HoodCompression setpointCompression;
    public Hood(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        compressor = hm.get(Servo.class, HardwareConstants.SHOOTER_COMPRESSION_SERVO.getId());
        setpointCompression = HoodCompression.POLLEN;
        displayTelemetry("Hood Compression", ()-> setpointCompression);
        displayTelemetry("Hood Angle", compressor::getPosition);
    }

    public void setupNectar() {
        setCompression(HoodCompression.NECTAR);
    }

    public void setupPollen() {
        setCompression(HoodCompression.POLLEN);
    }

    public void setCompression(HoodCompression comp) {
        if(setpointCompression == comp) return;
        setpointCompression = comp;
    }

    public HoodCompression getCompression() {
        return setpointCompression;
    }

    @Override
    public void loop() {
        compressor.setPosition(setpointCompression.getPosition());
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        if(g1.squareWasPressed()) {
            setupPollen();
        }
        if(g1.circleWasPressed()) {
            setupNectar();
        }
        telemetryM.addLine("G1 / SQUARE : Setup for Pollen");
        telemetryM.addLine("G1 / CIRCLE : Setup for Nectar");
    }
}
