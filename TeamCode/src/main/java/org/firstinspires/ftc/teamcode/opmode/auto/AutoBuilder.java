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
import org.firstinspires.ftc.teamcode.helpers.SpicyMode;
import org.firstinspires.ftc.teamcode.mechanism.Intake;
import org.firstinspires.ftc.teamcode.mechanism.Shooter;
import org.firstinspires.ftc.teamcode.mechanism.Transfer;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import java.util.ArrayList;

public class AutoBuilder extends SpicyMode {
    public AutoBuilder(Globals.MatchSide side) {
        super(side, Globals.ModeType.AUTO);
    }

    enum AutoState {
        _NEXT_CMD,
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

    ArrayList<AutoState> commands;
    int currCommandIndex = 0;

    AutoState state;

    PoseFactory p;
    Follower f;
    Shooter s;
    Transfer t;
    Intake i;

    Pose start;
    Pose garden;
    Pose spikeMarkPickup;
    Pose testPose;
    Path startToSpikeMark() {
        return Paths.line(start, garden).linear(start, garden);
    }
    Path pickupFromSpikeMark() {
        return Paths.curve(garden, spikeMarkPickup);
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
        state = AutoState._NEXT_CMD;
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

        garden = p.of(13, 12, 245);
        spikeMarkPickup = p.of(9, 9, 270);
        testPose = p.of(10, 10, 0);


        commands = new ArrayList<>();
        currCommandIndex = 0 ;

        // here I removed the state .BEGIN because we can just handle begin in start()

        telemetry.addData("We're on", side.name());
        telemetryM.update(telemetry);
    }
    @Override
    public void init_loop(){
        boolean hasEnd = commands.get(commands.size() - 1).equals(AutoState.END);
        if (!hasEnd) {
            if (gamepad1.circleWasPressed()) commands.add(AutoState.RIGHT_SHOOT);

            if (gamepad1.squareWasPressed()) commands.add(AutoState.LEFT_SHOOT);

            if (gamepad1.dpadDownWasPressed()) commands.add(AutoState.DOWN_FLOWER);

            if (gamepad1.dpadLeftWasPressed()) commands.add(AutoState.LEFT_FLOWER);


            if (gamepad1.dpadRightWasPressed()) commands.add(AutoState.GARDEN);

            if (gamepad1.rightBumperWasPressed())   // Park on the right side of the zone
                commands.add(AutoState.RIGHT_PARK);

            if (gamepad1.leftBumperWasPressed())    // Park on the left side of the zone
                commands.add(AutoState.LEFT_PARK);

            if (gamepad1.xWasPressed())             // Wait 1 second
                commands.add(AutoState.WAIT_ONE);

            if (gamepad1.touchpadWasPressed())
                commands.add(AutoState.TEST_POSE);

            if (gamepad1.dpad_right && gamepad1.options)   // Reset Path
                commands.clear();

            if (gamepad1.shareWasPressed())    // Remove last command
                commands.remove(commands.size()-1);

            if (gamepad1.left_trigger > 0.5 && gamepad1.right_trigger > 0.5 && gamepad1.triangle)
                commands.add(AutoState.END);
            
            telemetry.addLine("Auto building, waiting for END");
            telemetry.addData("Auto", commands);
        } else {
            telemetry.addLine("Auto has an END command, press G1 SHARE to undo.");
            telemetry.addData("Auto", commands);

            if (gamepad1.shareWasPressed()) commands.remove(commands.size()-1);
        }

        telemetry.update();
    }

    @Override
    public void start() {
        setState(commands.get(currCommandIndex)); // CCI is 0, so we just do the first action.
    }

    public void fsmRoutine() {
        if (currCommandIndex == commands.size()) // Insurance in case we forget to add END
            setState(AutoState.END);

        switch(state) {
            case _NEXT_CMD:
                setState(commands.get(++currCommandIndex));
            case GARDEN:
                f.follow(Paths.line(f.pose(), garden).linear(f.pose(), garden));
                setState(AutoState.WAIT_FOR_PATH);
                break;
            case RIGHT_SHOOT:
                f.follow(Paths.line(f.pose(), start).linear(f.pose(), start));
                setState(AutoState.WAIT_FOR_PATH);
                break;
            case TEST_POSE:
                f.follow(Paths.line(f.pose(), testPose).linear(f.pose(), testPose));
                setState(AutoState.WAIT_FOR_PATH);
                break;
            case WAIT_FOR_PATH:
                if (!f.isBusy()) {
                    setState(AutoState._NEXT_CMD);
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

        telemetry.addData("Current Command:", state);
        telemetry.addData("Auto", commands);
        telemetry.update();
        telemetryM.update(telemetry);
    }

    void setState(AutoState state) {
        this.state = state;
    }

    @Override
    public String getOpModeName() {
        return "AUTO BUILD... %s";
    }
}
