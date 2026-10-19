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
        c.xPodOffset.set(-0.2827621069480115);
        c.yPodOffset.set(0.08753058478588195);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });
    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.31830640661892085);
                Controller secondaryTranslationalForward = Controller.proportional(0.11760571194186198);
                Controller primaryTranslationalLateral = Controller.proportional(0.43718320078007783);
                Controller secondaryTranslationalLateral = Controller.proportional(0.16152751093796802);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.013313611270364407));
                c.brake.set(Controller.proportionalFeedforward(0.011316569579809746));

                c.headingFeedback.set(Controller.proportional(6.243866365638539));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.056822027472596105, 0.005384309915198298));

                c.linearBrakeCoefficients.set(Matrix.diag(0.07682628235366674, 0.05523619488672971));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.001396914392572265, 0.001688400582936964));

                c.maxAchievableForwardVelocity.set(74.05139551981057);
                c.maxAchievableStrafeVelocity.set(58.59306442079797);
                c.naturalForwardDeceleration.set(40.063172032706014);
                c.naturalStrafeDeceleration.set(68.25779232635912);
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