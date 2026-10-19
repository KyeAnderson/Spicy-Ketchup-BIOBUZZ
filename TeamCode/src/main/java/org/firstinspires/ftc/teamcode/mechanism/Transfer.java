package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Transfer extends Mechanism {
    public enum TransferVelocity {
        OFF(0),
        ON(1500);
        final int power;
        TransferVelocity(int i) {
            power = i;
        }
    }
    TransferVelocity transferVelo;
    DcMotorEx transfer;
    public Transfer(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        transfer = hm.get(DcMotorEx.class, "tf");
        displayTelemetry("Transfer Velocity", transfer::getVelocity);
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
        transfer.setVelocity(transferVelo.power);
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {

    }
}
