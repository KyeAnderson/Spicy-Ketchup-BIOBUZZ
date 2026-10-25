package org.firstinspires.ftc.teamcode.opmode.auto;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.utils.Timer;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.helpers.HoodCompression;
import org.firstinspires.ftc.teamcode.helpers.IntakeVelocity;
import org.firstinspires.ftc.teamcode.helpers.SpicyMode;
import org.firstinspires.ftc.teamcode.helpers.TransferVelocity;
import org.firstinspires.ftc.teamcode.mechanism.Intake;
import org.firstinspires.ftc.teamcode.mechanism.Shooter;
import org.firstinspires.ftc.teamcode.mechanism.Transfer;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import java.util.concurrent.TimeUnit;

public class AutoUno extends SpicyMode {
    public AutoUno(Globals.MatchSide side) {
        super(side, Globals.ModeType.AUTO);
    }

    enum AutoState {
        BEGIN,
        MOVE_TO_SCORE,
        BEGIN_SCORING,
        MOVE_TO_SPIKE_MARK,
        PICKUP_FROM_SPIKE_MARK,
        END
    }
    AutoState state;

    PoseFactory p;
    Follower f;
    Shooter s;
    Transfer t;
    Intake i;

    Pose start;
    Pose spikeMark;
    Pose spikeMarkPickup;
    Path startToSpikeMark() {
        return Paths.curve(start, p.of(13, 21, 270), spikeMark).linear(start, spikeMark);
    }
    Path pickupFromSpikeMark() {
        return Paths.line(spikeMark, spikeMarkPickup).linear(spikeMark, spikeMarkPickup);
    }
    Path moveToScore() {
        return Paths.line(spikeMarkPickup, start).linear(spikeMarkPickup, start);
    }

    @Override
    public void onBlueInit() {
        p = p.mirrorAroundPoint(70.75,70.75);
        telemetry.addLine("blue");
    }
    boolean flywheelReadyToShoot = false;
    Timer scoreTimer = new Timer();

    TelemetryManager telemetryM;

    @Override
    public void init() {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        state = AutoState.BEGIN;
        p = PoseFactory.degrees();
        super.init();
        f = Constants.create(hardwareMap);
        s = new Shooter(hardwareMap, telemetry);
        t = new Transfer(hardwareMap, telemetry);
        i = new Intake(hardwareMap, telemetry);
        flywheelReadyToShoot = false;
        start = p.of(58,10,0);
        f.setPose(start);
        f.update();

        spikeMark = p.of(20, 40, 270);
        spikeMarkPickup = p.of(12.7, 11, 270);

        telemetry.addData("We're on", side.name());
        telemetryM.update(telemetry);
    }

    public void fsmRoutine() {
        switch(state) {
            case BEGIN:
                setState(AutoState.MOVE_TO_SCORE);
                break;
            case MOVE_TO_SCORE:
                s.setCompression(HoodCompression.POLLEN);
                if(!f.isBusy()) {
                    setState(AutoState.BEGIN_SCORING);
                }
                break;
            case BEGIN_SCORING:
                s.setVelocity(Shooter.TESTING_SHOOTER_VELO);
                if(s.flywheelAtTarget()) {
                    if(!flywheelReadyToShoot) {
                        flywheelReadyToShoot = true;
                        scoreTimer.reset();
                    }
                    t.setVelocity(TransferVelocity.ON);
                } else {
                    t.stop();
                }
                if(flywheelReadyToShoot && scoreTimer.get(TimeUnit.MILLISECONDS) >= s.getCompress().getTime() * 4) {
                    // If we've scored (3 pollen, estimated score time)
                    t.stop();
                    s.setVelocity(0);
                    flywheelReadyToShoot = false;
                    f.follow(startToSpikeMark());
                    setState(AutoState.MOVE_TO_SPIKE_MARK);
                }
                break;
            case MOVE_TO_SPIKE_MARK:
                i.setVelocity(IntakeVelocity.SLOW);
                if(!f.isBusy()) {
                    f.follow(pickupFromSpikeMark());
                    setState(AutoState.PICKUP_FROM_SPIKE_MARK);
                }
                break;
            case PICKUP_FROM_SPIKE_MARK:
                i.setVelocity(IntakeVelocity.ON);
                if(!f.isBusy()) {
                    f.follow(moveToScore());
                    i.setVelocity(IntakeVelocity.OFF);
                    setState(AutoState.MOVE_TO_SCORE);
                }
                break;
            case END:
                break;
        }
    }

    @Override
    public void loop() {
        telemetryM.addLine(state.name());
        telemetryM.addLine(f.toString());
        telemetryM.addLine(i.toString());
        telemetryM.addLine(s.toString());
        telemetryM.addLine(t.toString());
        fsmRoutine();


        f.update();
        i.loop();
        s.loop();
        t.loop();

        telemetryM.update(telemetry);
        telemetry.update();
    }

    void setState(AutoState state) {
        this.state = state;
    }

    @Override
    public String getOpModeName() {
        return "AUTO 1... %s";
    }
}
