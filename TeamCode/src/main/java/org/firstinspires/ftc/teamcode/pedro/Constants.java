package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("lf");
        c.frontRightName.set("rf");
        c.backLeftName.set("lb");
        c.backRightName.set("rb");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
    });
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pin");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-4.113382054126169);
        c.yPodOffset.set(-8.224171315591168);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });
    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.28841280926380747);
                Controller secondaryTranslationalForward = Controller.proportional(0.10656082649078008);
                Controller primaryTranslationalLateral = Controller.proportional(0.5028910804551519);
                Controller secondaryTranslationalLateral = Controller.proportional(0.18580481673102692);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.011098798724775608));
                c.brake.set(Controller.proportionalFeedforward(0.009433978916059266));

                c.headingFeedback.set(Controller.proportional(5.39759634321417));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.05212535499061556, 0.005593726968255353));

                c.linearBrakeCoefficients.set(Matrix.diag(0.11442118794318075, 0.08419153768089584));
                c.quadraticBrakeCoefficients.set(Matrix.diag(7.220715893551591E-4, 9.933636727751955E-4));

                c.maxAchievableForwardVelocity.set(85.38019838889475);
                c.maxAchievableStrafeVelocity.set(66.16793631794056);
                c.naturalForwardDeceleration.set(32.08045278737966);
                c.naturalStrafeDeceleration.set(69.81373991690998);
            }
    );
    public static Follower create(HardwareMap h) {
         return new Follower(
                 new PinpointLocalizer(h, localizerConfig),
                 new Mecanum(h, drivetrainConfig),
                 new Foresight(foresightConfig)
         );
//        return null;
    }
}