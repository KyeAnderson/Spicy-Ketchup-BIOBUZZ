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
    DcMotorEx transfer;
    public enum HoodCompression  {
        POLLEN(0.6),
        NECTAR(0.45);
        final double pos;
        HoodCompression(double i) {
            pos = i;
        }
    }
    public enum ShooterVelocity {
        OFF(0),
        ON(1500),
        SLOW(500);
        final int power;
        ShooterVelocity(int i) {
            power = i;
        }
    }
    public enum TransferVelocity {
        OFF(0),
        ON(1500);
        final int power;
        TransferVelocity(int i) {
            power = i;
        }
    }
    HoodCompression compress;
    TransferVelocity transferVelo;
    float velocity;

    public Shooter(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        flywheel = hm.get(DcMotorEx.class, "fly");
        angle = hm.get(Servo.class, "ang");
        transfer = hm.get(DcMotorEx.class, "tf");
        velocity = ShooterVelocity.OFF.power;
        compress = HoodCompression.POLLEN;
        transferVelo = TransferVelocity.OFF;
        displayTelemetry("Hood Compression", ()->compress);
        displayTelemetry("Hood Angle", angle::getPosition);
        displayTelemetry("flywheel velocity", flywheel::getVelocity);
        displayTelemetry("Transfer Velocity", transfer::getVelocity);
        initializeMotors(flywheel, transfer);
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        transfer.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        setVelocity(ShooterVelocity.OFF);
    }

    public DcMotorEx getTransfer() {
        return transfer;
    }

    public void setCompression(HoodCompression comp) {
        if(compress == comp) return;
        compress = comp;
    }

    public void setTransfer(TransferVelocity velo) {
        if(transferVelo == velo) return;
        transferVelo = velo;
    }

    public void setVelocity(ShooterVelocity velo) {
        if(velocity == velo.power) return;
        velocity = velo.power;
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
        transfer.setVelocity(transferVelo.power);
    }

    double a = 0;
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
