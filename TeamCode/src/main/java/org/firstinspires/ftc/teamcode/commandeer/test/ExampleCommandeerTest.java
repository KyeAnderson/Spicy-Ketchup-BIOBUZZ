package org.firstinspires.ftc.teamcode.commandeer.test;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.commandeer.CQueue;
import org.firstinspires.ftc.teamcode.commandeer.commands.FollowPath;
import org.firstinspires.ftc.teamcode.helpers.MatchSide;
import org.firstinspires.ftc.teamcode.helpers.SpicyMode;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

public class ExampleCommandeerTest extends SpicyMode{
    CQueue queue = new CQueue();
    Follower f = Constants.createFollower(hardwareMap);
    public ExampleCommandeerTest(MatchSide side) {
        super(side);
    }

    @Override
    public void init() {
        super.init();

        queue.push(FollowPath.lineThroughPoses(f,
                f::getPose, // From
                ()->new Pose(0, 67,0), // To
                true));
    }

    @Override
    public void onRedInit() {

    }

    @Override
    public void onBlueInit() {

    }

    @Override
    public String getOpModeName() {
        return "";
    }

    @Override
    public void loop() {
        queue.update(); // CQueue acts as a state machine, so our first command to line through poses will happen and that'll be it
    }
}
