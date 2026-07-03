package org.firstinspires.ftc.teamcode.commandeer.commands;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import org.firstinspires.ftc.teamcode.commandeer.CCommand;
import org.firstinspires.ftc.teamcode.commandeer.CState;

import java.util.function.Supplier;

public class FollowPath extends CCommand {

    public static HeadingInterpolator linear(double pointA, double pointB, double endT) {
        return HeadingInterpolator.linear(pointA ,pointB, endT);
    }

    public FollowPath linearSetEndT(double endT) {
        interpolator = linear(pointA.get().getHeading(), pointB.get().getHeading(), endT);
        return this;
    }

    Follower f;
    boolean holdEnd;
    double power = 1.0;
    Supplier<Pose> pointA;
    Supplier<Pose> pointB;
    public FollowPath(Follower f,Object... args) {
        super(()->CState.ERROR, args);
        this.f = f;
    }

    public static FollowPath lineThroughPoses(Follower f, Supplier<Pose> from, Supplier<Pose> to, boolean holdEnd) {
        return builder(f).setPathChainSimple(from, to).setHoldEnd(holdEnd).build();
    }


    public static FollowPath lineThroughPoses(Follower f, Supplier<Pose> a, Supplier<Pose> b, boolean holdEnd, double power) {
        return builder(f).setPathChainSimple(a, b).setHoldEnd(holdEnd).setPower(power).build();
    }

    HeadingInterpolator interpolator;

    public FollowPath setInterpolator(HeadingInterpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public FollowPath setPathChainSimple(Supplier<Pose> pointA, Supplier<Pose> pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
        return this;
    }

    public FollowPath setPower(double p) {
        this.power = p;
        return this;
    }

    public FollowPath setHoldEnd(boolean holdEnd) {
        this.holdEnd = holdEnd;
        return this;
    }

    public FollowPath build() {
        this.setCommand(() ->{
            if(f.isBusy()) return CState.WORKING;
            if(f.atParametricEnd()) {
                f.setMaxPower(1);
                return CState.END;
            }
            return CState.WORKING;
        });
        return this;
    }

    public static FollowPath builder(Follower f) {
        return new FollowPath(f);
    }

    @Override
    public void start() {
        super.start();
        f.breakFollowing();
        f.setMaxPower(power);
        if(interpolator == null) {
            interpolator = linear(pointA.get().getHeading(), pointB.get().getHeading(), 1);
        }
        f.followPath(f.pathBuilder()
                        .addPath(
                                new BezierLine(
                                        pointA.get(),
                                        pointB.get()
                                )
                        )
                        .setHeadingInterpolation(interpolator)
                        .build()
                , holdEnd);
    }
}