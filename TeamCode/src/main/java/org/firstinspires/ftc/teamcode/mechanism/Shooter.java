package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Shooter extends Mechanism {
    DcMotorEx flywheel;
    Servo angle;
    DcMotorEx transfer;
    public enum ShooterVelocity {
        OFF(0),
        ON(1500),
        SLOW(500);
        final int power;
        ShooterVelocity(int i) {
            power = i;
        }
    }
    ShooterVelocity velocity;

    public Shooter(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        flywheel = hm.get(DcMotorEx.class, "flywheel");
        angle = hm.get(Servo.class, "servo");
        transfer = hm.get(DcMotorEx.class, "transfer");
        velocity = ShooterVelocity.OFF;
        this.displayTelemetry("Hood Angle", angle::getPosition);
        this.displayTelemetry("flywheel velocity", flywheel::getVelocity);
        this.displayTelemetry("Transfer Velocity", transfer::getVelocity);
        resetMotor(true, flywheel, transfer);
        setVelocity(ShooterVelocity.OFF);
    }

    public void setVelocity(ShooterVelocity velo) {
        if(velocity == velo) return;
        velocity = velo;
    }

    @Override
    public void loop() {
        flywheel.setVelocity(velocity.power);
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        if(g1.squareWasPressed()) {
            setVelocity(ShooterVelocity.OFF);
        }
        if(g1.circleWasPressed()) {
            setVelocity(ShooterVelocity.ON);
        }
        
        if(g1.triangleWasPressed()) {
            angle.setPosition(0.5);
        }

        telemetryM.addLine("Press square to turn off");
        telemetryM.addLine("Press circle to turn on");
        telemetryM.addLine("triangle to set 0.5");
    }
}
