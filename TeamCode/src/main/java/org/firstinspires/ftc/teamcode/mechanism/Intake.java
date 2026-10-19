package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Intake extends Mechanism {
    DcMotorEx motor;
    public enum IntakeVelocity {
        OFF(0),
        ON(5000),
        SLOW(500);
        final int power;
        IntakeVelocity(int i) {
            power = i;
        }
    }
    IntakeVelocity velocity;

    public Intake(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        motor = hm.get(DcMotorEx.class, "spin");
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        displayTelemetry("Velo", motor::getVelocity);
        velocity = IntakeVelocity.OFF;
        resetMotor(true, motor);
    }

    public void setVelocity(IntakeVelocity velo) {
        if(velocity == velo) return;
        velocity = velo;
    }

    @Override
    public void loop() {
        motor.setVelocity(velocity.power);
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        if(g1.squareWasPressed()) {
            setVelocity(IntakeVelocity.OFF);
        }
        if(g1.circleWasPressed()) {
            setVelocity(IntakeVelocity.ON);
        }

        telemetryM.addLine("Press square to turn off");
        telemetryM.addLine("Press circle to turn on");
    }
}
