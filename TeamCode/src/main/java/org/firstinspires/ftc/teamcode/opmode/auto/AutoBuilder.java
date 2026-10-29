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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AutoBuilder extends SpicyMode {
    public AutoBuilder(Globals.MatchSide side) {
        super(side, Globals.ModeType.AUTO);
    }

    enum AutoState {
        BEGIN,
        RIGHT_SHOOT,
        LEFT_SHOOT,
        DOWN_FLOWER,
        LEFT_FLOWER,
        GARDEN,
        RIGHT_PARK,
        LEFT_PARK,
        WAIT_ONE,
        TEST_POSE,
        END,
        WAIT_FOR_PATH
    }

    ArrayList<AutoState> cPath;
    int cPathDex;

    AutoState state;

    PoseFactory p;
    Follower f;
    Shooter s;
    Transfer t;
    Intake i;

    Pose start;
    Pose spikeMark;
    Pose spikeMarkPickup;
    Pose testPose;
    Path startToSpikeMark() {
        return Paths.line(start, spikeMark).linear(start, spikeMark);
    }
    Path pickupFromSpikeMark() {
        return Paths.curve(spikeMark, spikeMarkPickup);
      //  return Paths.line(spikeMark, spikeMarkPickup).linear(spikeMark, spikeMarkPickup);
    }
    Path moveToScore() {
        return Paths.line(f.pose(), start).linear(f.pose(), start);
    }

    @Override
    public void onBlueInit() {
        p = p.mirrorAroundPoint(70.75,70.75);
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
        start = p.of(56,9,0);
        f.setPose(start);
        f.update();

        spikeMark = p.of(13, 12, 245);
        spikeMarkPickup = p.of(9, 9, 270);
        testPose = p.of(10, 10, 0);


        cPath = new ArrayList<>();
        cPath.add(AutoState.BEGIN);
        cPathDex=0;

        telemetry.addData("We're on", side.name());
        telemetryM.update(telemetry);
    }
    @Override
    public void init_loop(){
        if (!cPath.get(cPath.size()-1).equals(AutoState.END)){
            if (gamepad1.circleWasPressed())
                cPath.add(AutoState.RIGHT_SHOOT);
            if (gamepad1.squareWasPressed())
                cPath.add(AutoState.LEFT_SHOOT);
            if (gamepad1.dpadDownWasPressed())
                cPath.add(AutoState.DOWN_FLOWER);
            if (gamepad1.dpadLeftWasPressed())
                cPath.add(AutoState.LEFT_FLOWER);
            if (gamepad1.dpadRightWasPressed())
                cPath.add(AutoState.GARDEN);
            if (gamepad1.rightBumperWasPressed())   // Park on the right side of the zone
                cPath.add(AutoState.RIGHT_PARK);
            if (gamepad1.leftBumperWasPressed())    // Park on the left side of the zone
                cPath.add(AutoState.LEFT_PARK);
            if (gamepad1.xWasPressed())             // Wait 1 second
                cPath.add(AutoState.WAIT_ONE);
            if (gamepad1.dpad_right && gamepad1.options)   // Reset Path
                cPath.clear();
            if (gamepad1.touchpadWasPressed())
                cPath.add(AutoState.TEST_POSE);
            if (gamepad1.shareWasPressed())    // Remove last command
                cPath.remove(cPath.size()-1);
            if (gamepad1.left_trigger > 0.5 && gamepad1.right_trigger > 0.5 && gamepad1.triangle)
                cPath.add(AutoState.END);

            telemetry.addData("Auto", cPath);
        }
    }

    public void fsmRoutine() {
 //       if (cPathDex == cPath.size()) // Insurance in case we forget to add END
//            setState(AutoState.END);
        switch(state) {
            case BEGIN:
                cPathDex++;
                setState(cPath.get(cPathDex));
                break;
            case GARDEN:
                f.follow(Paths.line(f.pose(), spikeMark).linear(f.pose(), spikeMark));
              //  f.follow(Paths.line(f.pose(), spikeMarkPickup).linear(f.pose(), spikeMarkPickup));
                cPathDex++;
                setState(AutoState.WAIT_FOR_PATH);
                break;
            case RIGHT_SHOOT:
                f.follow(Paths.line(f.pose(), start).linear(f.pose(), start));
                cPathDex++;
                setState(AutoState.WAIT_FOR_PATH);
                break;
            case TEST_POSE:
                f.follow(Paths.line(f.pose(), testPose).linear(f.pose(), testPose));
                cPathDex++;
                setState(AutoState.WAIT_FOR_PATH);
                break;
            case WAIT_FOR_PATH:
                if (!f.isBusy()) {
                    setState(AutoState.BEGIN);
                }
                break;
            case END:
                break;
        }
    }

    @Override
    public void loop() {
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
        telemetry.addData("Auto", cPath);
        telemetry.update();
    }

    void setState(AutoState state) {
        this.state = state;
    }

    @Override
    public String getOpModeName() {
        return "AUTO BUILD... %s";
    }
}
