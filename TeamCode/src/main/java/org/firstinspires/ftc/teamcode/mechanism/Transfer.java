package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.helpers.TransferVelocity;

public class Transfer extends Mechanism {
    TransferVelocity transferVelo;
    DcMotorEx transfer;
    public Transfer(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        transfer = hm.get(DcMotorEx.class, "tf");
        displayTelemetry("Current Velocity", transfer::getVelocity);
        displayTelemetry("Setpoint Velocity", transferVelo::getPower);
        transferVelo = TransferVelocity.OFF;
        transfer.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        initializeMotors(transfer);
    }

    public void setVelocity(TransferVelocity velo) {
        if(transferVelo == velo) return;
        transferVelo = velo;
    }

    public void stop() {
        setVelocity(TransferVelocity.OFF);
    }

    @Override
    public void loop() {
        transfer.setVelocity(transferVelo.getPower());
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        telemetryM.addLine("TransferVelocity.ON = " + TransferVelocity.ON.getPower());
        telemetryM.addLine("TransferVelocity.OFF = " + TransferVelocity.OFF.getPower());
        telemetryM.addLine("Gamepad 1 | Square: set velocity to TransferVelocity.ON");
        telemetryM.addLine("Gamepad 1 | Circle: set velocity to TransferVelocity.OFF");
        if(g1.squareWasPressed()) {
            setVelocity(TransferVelocity.ON);
        }
        if(g1.circleWasPressed()) {
            setVelocity(TransferVelocity.OFF);
        }

    }
}
