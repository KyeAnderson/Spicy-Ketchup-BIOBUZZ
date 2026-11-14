package org.firstinspires.ftc.teamcode.opmode.auto;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.utils.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.mechanism.Hood;
import org.firstinspires.ftc.teamcode.mechanism.Intake;
import org.firstinspires.ftc.teamcode.mechanism.Shooter;
import org.firstinspires.ftc.teamcode.mechanism.Transfer;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.pedro.Drawing;

import java.util.concurrent.TimeUnit;

@Disabled
@Autonomous(name = "LM1 Match 2 AUTO...")
public class Match2Auto extends OpMode {
    enum AutoState {
        BEGIN,
        MOVE_TO_SCORE,
        BEGIN_SCORING,
        MOVE_TO_END,
        EXIT_OPMODE
    }
    AutoState state;

    PoseFactory p;
    Follower f;
    Shooter s;
    Hood h;
    Transfer t;
    Intake i;

    Pose start;
    Pose scorePose;
    Pose loadingZone;
    Path startToScore() {
        return Paths
                .line(f.pose(), scorePose)
                .linear(f.pose(), scorePose);
    }
    Path finalScoreToLoadingZone() {
        return Paths
                .curve(f.pose(), new Pose(62, 119, 0), loadingZone)
                .linear(f.pose(), loadingZone);
    }
    boolean pickedUpSpikeMark = false;
    boolean flywheelReadyToShoot = false;
    boolean DEBUG_MODE = true;
    Timer scoreTimer = new Timer();
    Timer stateTimer = new Timer();
    Timer waitTimer = new Timer();

    TelemetryManager telemetryM;

    @Override
    public void init() {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        state = AutoState.BEGIN;
        p = PoseFactory.degrees();
        f = Constants.create(hardwareMap);
        s = new Shooter(hardwareMap, telemetry);
        h = new Hood(hardwareMap, telemetry);
        t = new Transfer(hardwareMap, telemetry);
        i = new Intake(hardwareMap, telemetry);
        flywheelReadyToShoot = false;
        pickedUpSpikeMark = false;
        start = p.of(58,132,180); // pods of robot is y = 11.5, x = 1 if at corner (0,0)
        f.setPose(start);
        f.update();

        Globals.lastPose = f.pose();
        scorePose = p.of(61, 132, 179);

        loadingZone = p.of(11, 119, 270);

        telemetryM.update(telemetry);
    }

    public void start() {
        waitTimer.reset();
    }

    public void fsmRoutine() {
        switch(state) {
            case BEGIN:
                f.follow(startToScore());
                setState(AutoState.MOVE_TO_SCORE);
                break;
            case MOVE_TO_SCORE:
                h.setupPollen();
                if(waitTimer.get(TimeUnit.SECONDS) >=18) {
                    s.setVelocity(s.getVelocityForDist(f.pose().distance(Globals.redBasketAudienceGoal)));
                }
                if(!f.isBusy() && waitTimer.get(TimeUnit.SECONDS) >= 21) {
                    setState(AutoState.BEGIN_SCORING);
                }
                break;
            case BEGIN_SCORING:
                if(s.flywheelAtTarget()) {
                    if(!flywheelReadyToShoot) {
                        flywheelReadyToShoot = true;
                        scoreTimer.reset();
                    }
                    t.transfer();
                } else {
                    t.stop();
                }
                if(flywheelReadyToShoot && scoreTimer.get(TimeUnit.MILLISECONDS) >= h.getCompression().getTimeToScore() * 4) {
                    // If we've scored (3 pollen, estimated score time)
                    t.stop();
                    s.stop();
                    flywheelReadyToShoot = false;
                    f.follow(finalScoreToLoadingZone());
                    setState(AutoState.MOVE_TO_END);
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

        Drawing.drawCurrent(f);

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
}
