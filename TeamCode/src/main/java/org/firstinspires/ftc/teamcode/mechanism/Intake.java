package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.helpers.HardwareConstants;

public class Intake extends Mechanism {
    public enum IntakeVelocity {
        OFF(0),
        INTAKE(5000),
        OUTTAKE(-5000),
        SLOW(500);
        final int velocity;
        IntakeVelocity(int i) {
            velocity = i;
        }

        public int getVelocity() {
            return velocity;
        }
    }

    DcMotorEx motor;
    IntakeVelocity velocity;

    public Intake(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        motor = hm.get(DcMotorEx.class, HardwareConstants.INTAKE_MOTOR.getId());
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        displayTelemetry("Setpoint Velocity", ()->velocity);
        displayTelemetry("Current Velocity", motor::getVelocity);
        velocity = IntakeVelocity.OFF;
        resetMotor(true, motor);
    }

    public void setVelocity(IntakeVelocity nextVelocity) {
        if(velocity == nextVelocity) return;
        velocity = nextVelocity;
    }

    public void slowIntake() {setVelocity(IntakeVelocity.SLOW);}
    public void intake() {setVelocity(IntakeVelocity.INTAKE);}
    public void outtake() {setVelocity(IntakeVelocity.OUTTAKE);}
    public void stop() {
        setVelocity(IntakeVelocity.OFF);
    }

    @Override
    public void loop() {
        motor.setVelocity(velocity.getVelocity());
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        if(g1.circleWasPressed()) intake();
        if(g1.crossWasPressed()) slowIntake();
        if(g1.triangleWasPressed()) outtake();
        if(g1.squareWasPressed()) stop();

        telemetryM.addLine("G1 / Circle : Intake");
        telemetryM.addLine("G1 / Cross : Slow Intake");
        telemetryM.addLine("G1 / Triangle : Outtake");
        telemetryM.addLine("G1 / Square : Stop");
    }
}
