package org.firstinspires.ftc.teamcode.katchup;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PIDFController;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mechanism.Mechanism;

import java.util.Locale;

public class SwervePod extends Mechanism{
    public static enum ControlMode {
        POSITION,
        VELOCITY
    }
    ControlMode mode;
    DcMotorEx motor;
    PIDFController controller;
    double ticksPerUnit = 0;

    public SwervePod(HardwareMap hw, Telemetry tele, String device, PIDFCoefficients coefficients, ControlMode mode) {
        super(hw,tele);
        this.mode = mode;
        motor = hw.get(DcMotorEx.class, device);
        controller = new PIDFController(coefficients);
        Mechanism.resetMotor(true, motor);

        displayTelemetry("P/I/D/F", ()->String.format(Locale.ENGLISH, "%.2f/%.2f/%.2f/%.2f", controller.P(), controller.I(), controller.D(), controller.F()));
        displayTelemetry("Target Pos", controller::getTargetPosition);
        displayTelemetry("Current Pos", motor::getCurrentPosition);
        displayTelemetry("Error", controller::getError);
        displayTelemetry("Ticks per Unit", this::getTicksPerUnit);
    }

    public SwervePod(HardwareMap hw, Telemetry tele, String device, PIDFCoefficients coefficients, ControlMode mode, double tpu) {
        this(hw,tele, device, coefficients, mode);
        this.setTicksPerUnit(tpu);
    }

    public void setCoefficients(PIDFCoefficients e) {
        controller.setCoefficients(e);
    }

    public void setTicksPerUnit(double tpi) {
        ticksPerUnit = tpi;
    }

    public double getTicksPerUnit() {
        return ticksPerUnit;
    }

    public void setTargetPosition(double t) {
        controller.setTargetPosition(t);
    }

    public double getTargetPosition() {
        return controller.getTargetPosition();
    }

    public double getCurrentPosition() {
        return motor.getCurrentPosition();
    }

    double lastVelocity = 0;
    public void setVelocity(double in) {
        if(in == lastVelocity) return;
        if(Math.abs(in - lastVelocity) > 0.1) {
            motor.setVelocity(in);
            lastVelocity = in;
        }
    }

    @Override
    public void loop() {
        if(mode == ControlMode.POSITION) {
            controller.updatePosition(getCurrentPosition());

            // max 2800 tps
            motor.setVelocity(controller.run() * 50);
        }
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {}
}
