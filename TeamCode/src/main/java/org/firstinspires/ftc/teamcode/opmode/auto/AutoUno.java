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
        MOVE_TO_END,
        EXIT_OPMODE
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
    Pose scorePostPickup;
    Pose loadingZone;
    Path startToSpikeMark() {
        return Paths
                .curve(start,
                        p.of(13, 21, 270),
                        spikeMark)
                .linear(start, spikeMark);
    }
    Path pickupFromSpikeMark() {
        return Paths
                .line(spikeMark, spikeMarkPickup)
                .linear(spikeMark, spikeMarkPickup)
                .with(
                    Constants.foresightConfig.maxPathSpeed.at(0.5) // slow it down to pickup
                );
    }
    Path spikeMarkToScore() {
        return Paths
                .line(spikeMarkPickup, scorePostPickup)
                .linear(spikeMarkPickup, scorePostPickup);
    }
    Path finalScoreToLoadingZone() {
        return Paths
                .line(scorePostPickup, loadingZone)
                .linear(scorePostPickup, loadingZone);
    }
    @Override
    public void onBlueInit() {
        p = p.mirrorAroundPoint(70.75,70.75);
        telemetry.addLine("blue");
    }
    boolean pickedUpSpikeMark = false;
    boolean flywheelReadyToShoot = false;
    boolean DEBUG_MODE = true;
    Timer scoreTimer = new Timer();
    Timer stateTimer = new Timer();

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
        pickedUpSpikeMark = false;
        start = p.of(58,10,0);
        f.setPose(start);
        f.update();

        Globals.lastPose = f.pose();

        spikeMark = p.of(20, 40, 270);
        spikeMarkPickup = p.of(12.7, 11, 270);

        scorePostPickup = p.of(58, 125, 180);
        loadingZone = p.of(12, 96, 270);

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
                s.setVelocity(Shooter.TESTING_SHOOTER_VELO);
                if(!f.isBusy()) {
                    setState(AutoState.BEGIN_SCORING);
                }
                break;
            case BEGIN_SCORING:
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
                    s.stop();
                    flywheelReadyToShoot = false;
                    if(pickedUpSpikeMark) {
                        f.follow(finalScoreToLoadingZone());
                        setState(AutoState.MOVE_TO_END);
                    } else {
                        f.follow(startToSpikeMark());
                        setState(AutoState.MOVE_TO_SPIKE_MARK);
                    }
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
                    f.follow(spikeMarkToScore());
                    i.stop();
                    pickedUpSpikeMark = true;
                    setState(AutoState.MOVE_TO_SCORE);
                }
                break;
            case MOVE_TO_END:
                // Final cleanup
                i.stop();
                s.stop();
                t.stop();
                if(!f.isBusy()) {
                    setState(AutoState.EXIT_OPMODE);
                }
                break;
            case EXIT_OPMODE:
                // Final final cleanup
                requestOpModeStop();
                break;
        }
    }

    void debugRoutine() {
        telemetryM.addData("State", state.name());
        telemetryM.addData("Time in current state", stateTimer.get(TimeUnit.MILLISECONDS));
        telemetryM.addData("Score Timer", scoreTimer.get(TimeUnit.MILLISECONDS));
        telemetryM.addData("Pose", f.pose());
        telemetryM.addLine(i.toString());
        telemetryM.addLine(s.toString());
        telemetryM.addLine(t.toString());

        telemetryM.update(telemetry);
        telemetry.update();
    }

    @Override
    public void loop() {
        Globals.lastPose = f.pose();
        fsmRoutine();

        f.update();
        i.loop();
        s.loop();
        t.loop();

        if(DEBUG_MODE) debugRoutine();
        if(gamepad1.xWasPressed()) DEBUG_MODE = !DEBUG_MODE;
    }

    void setState(AutoState state) {
        this.state = state;
        stateTimer.reset();
    }

    @Override
    public String getOpModeName() {
        return "AUTO 1... %s";
    }
}
