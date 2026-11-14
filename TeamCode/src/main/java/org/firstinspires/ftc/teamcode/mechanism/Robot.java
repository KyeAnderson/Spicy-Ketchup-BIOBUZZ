package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.Drawing;

import java.util.concurrent.ThreadPoolExecutor;

public class Robot extends Mechanism {
    private final Follower follower;
    private final Intake intake;
    private final Shooter shooter;
    private final Transfer transfer;
    private final Hood hood;
    private final Turret turret;
    public Robot(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        follower = Constants.create(hardwareMap);
        intake = new Intake(hm, tele);
        shooter = new Shooter(hm, tele);
        transfer = new Transfer(hm, tele);
        hood = new Hood(hm, tele);
        turret = new Turret(hm, tele);
        displayTelemetry("Current Pose", follower::pose);
        displayTelemetry("Intake", intake);
        displayTelemetry("Shooter", shooter);
        displayTelemetry("Transfer", transfer);
        displayTelemetry("Hood", hood);
        displayTelemetry("Turret", turret);
    }

    public Follower follower() {return follower;}
    public Intake intake() {return intake;}
    public Shooter shooter() {return shooter;}
    public Transfer transfer() {return transfer;}
    public Hood hood() {return hood;}
    public Turret turret() {return turret;}
    public Pose currentPose() {return follower.pose();}

    public double shooterVelocityFor(Pose target) {
        return shooter.getVelocityForDist(follower.pose().distance(target));
    }
    public void drawRobotOnPanels() {
        Drawing.drawCurrent(follower);
    }

    @Override
    public void loop() {
        Globals.lastPose = currentPose();

        follower.update();
        intake.loop();
        shooter.loop();
        transfer.loop();
        hood.loop();
        turret.loop();
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {

    }
}
