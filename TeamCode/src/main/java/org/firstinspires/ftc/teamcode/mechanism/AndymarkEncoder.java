package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PIDFController;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.commandeer.CCommand;
import org.firstinspires.ftc.teamcode.commandeer.CQueue;
import org.firstinspires.ftc.teamcode.commandeer.CState;
import org.firstinspires.ftc.teamcode.commandeer.logical.LinkedGroup;
import org.firstinspires.ftc.teamcode.helpers.HardwareConstants;

public class AndymarkEncoder extends Mechanism {
    AnalogInput encoder;
    private final double maxVoltage;
    double offset = 0;

    DcMotorEx motor;
    PIDFController controller;
    public AndymarkEncoder(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        controller = new PIDFController(new PIDFCoefficients(
                5,
                0,
                0.00055,
                0
        ));

        encoder = hm.get(AnalogInput.class, "am");
        motor = hm.get(DcMotorEx.class, "turret");
        maxVoltage = encoder.getMaxVoltage();

        displayTelemetry("Max Voltage", encoder::getMaxVoltage);
        displayTelemetry("Cur Voltage", encoder::getVoltage);
        displayTelemetry("Enc Degrees", this::voltageToDegrees);
        displayTelemetry("Mot Degrees", this::motorToDegrees);
        displayTelemetry("Mot POS", motor::getCurrentPosition);
    }

    double motorToDegrees() {
        return (motor.getCurrentPosition() * (360 / 751.8));
    }

    double lastDeg;

    double voltageToDegrees() {
        return (encoder.getVoltage() * Math.PI / maxVoltage * 360 + offset);
    }

    double inc = 0;
    @Override
    public void loop() {
        double current = voltageToDegrees();

        double delta = current - lastDeg;
        if (delta > 180.0) {
            delta -= 360.0; // Rollover counter-clockwise (e.g., 5° - 355° = -350° -> +10°)
        } else if (delta < -180.0) {
            delta += 360.0; // Rollover clockwise (e.g., 355° - 5° = 350° -> -10°)
        }

        inc += delta;
        lastDeg = current;

        controller.setTargetPosition(inc);
        controller.updatePosition(motorToDegrees());

        motor.setVelocity(controller.run());

        lastDeg = current;
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
    }

}