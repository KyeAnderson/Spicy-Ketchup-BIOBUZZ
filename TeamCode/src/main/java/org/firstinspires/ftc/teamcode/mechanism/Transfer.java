package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImpl;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.helpers.TransferVelocity;

public class Transfer extends Mechanism {
    TransferVelocity transferVelo;
    DcMotorEx transfer;
    CRServo tServo;
    public Transfer(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        transfer = hm.get(DcMotorEx.class, "tf");
        tServo = hm.get(CRServoImpl.class, "ts");
        transferVelo = TransferVelocity.OFF;
        displayTelemetry("Transfer Velo", () -> transferVelo.name());
        displayTelemetry("Current Velocity", transfer::getVelocity);
        displayTelemetry("Current Servo Power", tServo::getPower);
        displayTelemetry("Setpoint Velocity", ()->transferVelo.getPower());
        displayTelemetry("Setpoint Servo Power", ()->transferVelo.getCPower());
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
        tServo.setPower(transferVelo.getCPower());
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
        if(g1.triangleWasPressed()) {
            setVelocity(TransferVelocity.FRONT_ON);
        }
        if(g1.circleWasPressed()) {
            setVelocity(TransferVelocity.OFF);
        }

    }
}
