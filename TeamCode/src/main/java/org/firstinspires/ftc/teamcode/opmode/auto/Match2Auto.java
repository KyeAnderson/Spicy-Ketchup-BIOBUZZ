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
import org.firstinspires.ftc.teamcode.mechanism.Robot;
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
    Robot robot;
    Pose start;
    Pose scorePose;
    Pose loadingZone;
    Path startToScore() {
        return Paths
                .line(robot.currentPose(), scorePose)
                .linear(robot.currentPose(), scorePose);
    }
    Path finalScoreToLoadingZone() {
        return Paths
                .curve(robot.currentPose(), new Pose(62, 119, 0), loadingZone)
                .linear(robot.currentPose(), loadingZone);
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
        robot = new Robot(hardwareMap, telemetry);
        flywheelReadyToShoot = false;
        pickedUpSpikeMark = false;
        start = p.of(58,132,180); // pods of robot is y = 11.5, x = 1 if at corner (0,0)
        robot.follower().setPose(start);
        robot.follower().update();

        Globals.lastPose = robot.currentPose();
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
                robot.follower().follow(startToScore());
                setState(AutoState.MOVE_TO_SCORE);
                break;
            case MOVE_TO_SCORE:
                robot.hood().setupPollen();
                if(waitTimer.get(TimeUnit.SECONDS) >=18) {
                    robot.shooter().setVelocity(robot.shooterVelocityFor(Globals.redBasketAudienceGoal));
                }
                if(!robot.follower().isBusy() && waitTimer.get(TimeUnit.SECONDS) >= 21) {
                    setState(AutoState.BEGIN_SCORING);
                }
                break;
            case BEGIN_SCORING:
                if(robot.shooter().flywheelAtTarget()) {
                    if(!flywheelReadyToShoot) {
                        flywheelReadyToShoot = true;
                        scoreTimer.reset();
                    }
                    robot.transfer().transfer();
                } else {
                    robot.transfer().stop();
                }
                if(flywheelReadyToShoot && scoreTimer.get(TimeUnit.MILLISECONDS) >= robot.hood().getCompression().getTimeToScore() * 4) {
                    // If we've scored (3 pollen, estimated score time)
                    robot.transfer().stop();
                    robot.shooter().stop();
                    flywheelReadyToShoot = false;
                    robot.follower().follow(finalScoreToLoadingZone());
                    setState(AutoState.MOVE_TO_END);
                }
                break;
            case MOVE_TO_END:
                // Final cleanup
                robot.intake().stop();
                robot.shooter().stop();
                robot.transfer().stop();
                if(!robot.follower().isBusy()) {
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
        telemetryM.addData("Pose", robot.currentPose());
        robot.showDebugInfoOn(telemetryM);

        robot.drawRobotOnPanels();

        telemetryM.update(telemetry);
        telemetry.update();
    }

    @Override
    public void loop() {
        fsmRoutine();

        robot.loop();

        if(DEBUG_MODE) debugRoutine();
        if(gamepad1.xWasPressed()) DEBUG_MODE = !DEBUG_MODE;
    }

    void setState(AutoState state) {
        this.state = state;
        stateTimer.reset();
    }
}
