package org.firstinspires.ftc.teamcode.opmode.auto;

import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.ivy.pedro.PedroCommands;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Disabled
@Autonomous(name="Command base test auto 1")
public class TestCommandBase extends OpMode {
    Follower f;
    PoseFactory p;
    Path startToMiddle;

    @Override
    public void init() {
        p = PoseFactory.degrees();
        f = Constants.create(hardwareMap);
        f.update();
        telemetry.update();

        Pose begin = p.of(56, 11, 90);
        Pose end = p.of(71, 79.5, 180);
        f.setPose(begin);
        startToMiddle = Paths.line(
                begin,
                end
        ).linear(begin, end);

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
}
