package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.commandeer.CCommand;
import org.firstinspires.ftc.teamcode.commandeer.CQueue;
import org.firstinspires.ftc.teamcode.commandeer.CState;
import org.firstinspires.ftc.teamcode.commandeer.logical.LinkedGroup;
import org.firstinspires.ftc.teamcode.commandeer.logical.WaitUntil;
import org.firstinspires.ftc.teamcode.helpers.HardwareConstants;

import java.util.function.Supplier;

public class TestMechanism extends Mechanism {
    Servo servo;
    private double position;

    public TestMechanism(HardwareMap hm, Telemetry tele) {
        super(hm, tele);

        servo = hm.get(Servo.class, HardwareConstants.TEST_SERVO.getId());
        position = servo.getPosition();
    }

    @Override
    public void loop() {
        if(position == servo.getPosition()) return;
        servo.setPosition(position);
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public double getPosition() {
        return position;
    }

    CQueue intermediate;

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        if(g1.triangleWasPressed()) {
            setPosition(0);
        }
        if(g1.crossWasPressed()) {
            setPosition(0.5);
        }
        if(g1.squareWasPressed()) {
            if(intermediate == null) intermediate = new CQueue();
            intermediate.push(new LinkedGroup(
                    new SetPosition(-1),
                    new SetPosition(-0.5),
                    new SetPosition(0),
                    new SetPosition(0.5),
                    new SetPosition(1)
            ));
        }
        intermediate.update();
        telemetryM.addLine("Set position of servo");
        telemetryM.addLine("Triangle: 0, Cross: 0.5");
        telemetryM.addLine("Square to test the command-system");
    }

    public class SetPosition extends CCommand {
        double position;

        public SetPosition(double position) {
            super(() -> (servo.getPosition() == position) ? CState.END : CState.WORKING); // Super's function is our state checker.
            if(servo == null) throw new RuntimeException("TestMechanism is not set up properly. Please initialize it!");
            this.position = position;
        }

        @Override
        public void start() {
            setPosition(position);
        }
    }
}