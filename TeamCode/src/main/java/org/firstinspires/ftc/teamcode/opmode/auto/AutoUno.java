package org.firstinspires.ftc.teamcode.opmode.auto;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.helpers.SpicyMode;
import org.firstinspires.ftc.teamcode.pedro.Constants;

public class AutoUno extends SpicyMode {
    Follower f;
    enum AutoState {
        INIT,
        MOVE,
        END
    }
    AutoState state;
    Path toMiddle;
    PoseFactory p;
    public AutoUno(Globals.MatchSide side) {
        super(side, Globals.ModeType.AUTO);
    }
    Pose start;
    Pose end;
    @Override
    public void onRedInit() {
        telemetry.addLine("This will only show up on RED.");
        f.setPose(new Pose(56, 11, Math.toRadians(90)));
        start= p.of(56,11,90);
        end = p.of(71,79.5,180);

    }
    Path path1() {
        return Paths.line(start, end).linear(start, end);
    }
    Path path2() {
        return Paths.line(end, start).linear(end, start);
    }

    @Override
    public void onBlueInit() {
        telemetry.speak("Bro do not select blue autonomous");
        telemetry.addLine("This will only show up on BLUE.");
    }

    @Override
    public void init() {
        state = AutoState.INIT;
        p = PoseFactory.degrees();
        f = Constants.create(hardwareMap);
        super.init();
        f.update();
        telemetry.addData("We're on", side.name());
        telemetry.update();
    }

    @Override
    public void loop() {
        switch(state) {
            case INIT:
                f.follow(path1());
                setState(AutoState.MOVE);
                break;
            case MOVE:
                if(!f.isBusy()) {
                    setState(AutoState.END);
                }
                break;
            case END:
                f.follow(path2());
                break;
        }
        f.update();
    }

    void setState(AutoState state) {
        this.state = state;
    }

    @Override
    public String getOpModeName() {
        return "AUTO 1... %s";
    }
}
