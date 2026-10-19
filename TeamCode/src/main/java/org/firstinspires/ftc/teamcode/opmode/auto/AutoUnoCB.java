package org.firstinspires.ftc.teamcode.opmode.auto;

import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.ivy.groups.Groups;
import com.pedropathing.ivy.pedro.PedroCommands;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.helpers.SpicyMode;
import org.firstinspires.ftc.teamcode.pedro.Constants;

public class AutoUnoCB extends SpicyMode {
    public AutoUnoCB(Globals.MatchSide side) {
        super(side, Globals.ModeType.AUTO);
    }
    Follower f;
    PoseFactory p;
    Path startToMiddle;
    @Override
    public void onRedInit() {
        Pose begin = p.of(56, 11, 90);
        Pose end = p.of(71, 79.5, 180);
        f.setPose(begin);
        startToMiddle = Paths.line(
                begin,
                end
        ).linear(begin, end);
    }

    @Override
    public void init() {
        p = PoseFactory.degrees();
        f = Constants.create(hardwareMap);
        super.init(); // setup side specific
        f.update();
        telemetry.addData("We're on", side.name());
        telemetry.update();
        Scheduler.reset();
        Scheduler.schedule(sequential(
                PedroCommands.follow(f, startToMiddle)
        ));
    }

    @Override
    public void loop() {
        Scheduler.execute();
        f.update();
    }

    @Override
    public String getOpModeName() {
        return "command base test %s";
    }
}
