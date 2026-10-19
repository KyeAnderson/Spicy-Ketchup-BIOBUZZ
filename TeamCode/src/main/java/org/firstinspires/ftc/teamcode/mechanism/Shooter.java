package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Shooter extends Mechanism {
    DcMotorEx flywheel;
    Servo angle;
    public enum HoodCompression  {
        POLLEN(0.6),
        NECTAR(0.45);
        final double pos;
        HoodCompression(double i) {
            pos = i;
        }
    }

    HoodCompression compress;
    float velocity;

    public Shooter(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        flywheel = hm.get(DcMotorEx.class, "fly");
        angle = hm.get(Servo.class, "ang");
        velocity = 0;
        compress = HoodCompression.POLLEN;
        displayTelemetry("Hood Compression", ()->compress);
        displayTelemetry("Hood Angle", angle::getPosition);
        displayTelemetry("Flywheel velocity", flywheel::getVelocity);
        initializeMotors(flywheel);
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setCompression(HoodCompression comp) {
        if(compress == comp) return;
        compress = comp;
    }

    public void setVelocity(float velo) {
        if(velocity == velo) return;
        velocity = velo;
    }

    public boolean flywheelAtTarget() {
        return Math.abs(flywheel.getVelocity() - velocity) < 50;
    }

    @Override
    public void loop() {
        flywheel.setVelocity(velocity);
        angle.setPosition(compress.pos);
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        if(g1.squareWasPressed()) {
            setCompression(HoodCompression.POLLEN);
        }
        if(g1.circleWasPressed()) {
            setCompression(HoodCompression.NECTAR);
        }


        telemetryM.addLine("Press square to turn off");
        telemetryM.addLine("Press circle to turn on");
        telemetryM.addLine("triangle to set 0.5");
    }
}
