package org.firstinspires.ftc.teamcode.mechanism;

import com.bylazar.telemetry.PanelsTelemetry;
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
    private final Flowermabob flowermabob;
    private final Hood hood;
    private final Turret turret;
    private final Vision vision;
    public Robot(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        follower = Constants.create(hardwareMap);
        vision = new Vision(hm, tele, this);
        intake = new Intake(hm, tele, this);
        shooter = new Shooter(hm, tele, this);
        transfer = new Transfer(hm, tele, this);
        hood = new Hood(hm, tele, this);
        turret = new Turret(hm, tele, this);
        flowermabob = new Flowermabob(hm, tele, this);
        displayTelemetry("Current Pose", follower::pose);
        displayTelemetry("Vision", vision);
        displayTelemetry("Intake", intake);
        displayTelemetry("Shooter", shooter);
        displayTelemetry("Transfer", transfer);
        displayTelemetry("Hood", hood);
        displayTelemetry("Turret", turret);
        displayTelemetry("Flowermabob", flowermabob);
    }

    public Follower follower() {return follower;}
    public Intake intake() {return intake;}
    public Shooter shooter() {return shooter;}
    public Transfer transfer() {return transfer;}
    public Hood hood() {return hood;}
    public Turret turret() {return turret;}
    public Flowermabob flowermabob() {return flowermabob;}
    public Pose currentPose() {return follower.pose();}

    public double shooterVelocityFor(Pose target) {
        return shooter.getVelocityForDist(follower.pose().distance(target));
    }
    public void drawRobotOnPanels() {
        Drawing.drawCurrent(follower);
    }
    public void showDebugInfoOnPanels() {
        showDebugInfoOn(PanelsTelemetry.INSTANCE.getTelemetry());
    }
    public void showDebugInfoOn(Telemetry telemetry) {
        telemetry.addLine(toString());
    }
    public void showDebugInfoOn(TelemetryManager telemetryM) {
        telemetryM.getLines().addAll(toStrings());
    }

    @Override
    public void loop() {
        Globals.lastPose = currentPose();

        follower.update();
        vision.loop();
        intake.loop();
        shooter.loop();
        transfer.loop();
        hood.loop();
        turret.loop();
        flowermabob.loop();
    }

    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {

    }
}
