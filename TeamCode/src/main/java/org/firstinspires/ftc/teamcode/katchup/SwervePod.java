package org.firstinspires.ftc.teamcode.katchup;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PIDFController;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mechanism.Mechanism;

public class SwervePod extends Mechanism{
    DcMotorEx motor;
    PIDFCoefficients coefficients = new PIDFCoefficients(0.1,0,0,0);
    PIDFController controller;
    double targetPosition = 0;
    double ticksPerUnit = 28;
    double lastPosition = 0;///////////

    public SwervePod(HardwareMap hw, Telemetry tele, String device) {
        super(hw,tele);
        motor = hw.get(DcMotorEx.class, device);
        controller = new PIDFController(coefficients);
        Mechanism.resetMotor(true, motor);

        displayTelemetry("Current targetpos", this::getTargetPosition);
        displayTelemetry("Current pos", motor::getCurrentPosition);
    }

    public void setCoefficients(PIDFCoefficients e) {
        this.coefficients = e;
        controller.setCoefficients(e);
    }

    public void setTicksPerInches(double tpi) {
        ticksPerUnit = tpi;
    }

    public double getTicksPerInches() {
        return ticksPerUnit;
    }

    public void setTargetPosition(double t) {
        targetPosition = t;
        controller.setTargetPosition(t);
    }

    public double getTargetPosition() {
        return targetPosition;
    }
    public double getLastPosition() {
            return lastPosition;
        }
    public double getCurrentPosition() {
        lastPosition = motor.getCurrentPosition();//////////////
        return lastPosition;
    }

    @Override
    public void loop() {
        controller.updatePosition(motor.getCurrentPosition());

        // max 2800 tps
        motor.setVelocity(controller.run() * 50);
    }


    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        if(g1.xWasPressed()) {
            setTargetPosition(500);
        }
        if(g1.yWasPressed()) {
            setTargetPosition(1000);
        }
    }
}
