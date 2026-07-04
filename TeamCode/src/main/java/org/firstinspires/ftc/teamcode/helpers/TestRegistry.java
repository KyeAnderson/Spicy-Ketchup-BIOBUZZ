package org.firstinspires.ftc.teamcode.helpers;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.teamcode.mechanism.Mechanism;
import org.firstinspires.ftc.teamcode.mechanism.TestMechanism;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.pedroPathing.Drawing;

import java.lang.reflect.InvocationTargetException;

public class TestRegistry {
    static class GenericTest extends OpMode {
        public Class<? extends Mechanism> mechanismClass;
        Mechanism mechanism;
        Follower follower;
        TelemetryManager telemetryM;

        public GenericTest(Class<? extends Mechanism> m) {
            super();
            this.mechanismClass = m;
        }

        @Override
        public void init() {
            telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
            try {
                mechanism = mechanismClass.getConstructor(HardwareMap.class, Telemetry.class).newInstance(hardwareMap, telemetry);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                     NoSuchMethodException e) {
                telemetryM.addLine("ERROR: Failed to construct " + mechanismClass.getSimpleName());
                telemetryM.addLine(String.valueOf(e));
                telemetryM.update(telemetry);
                return;
            }
            follower = Constants.createFollower(hardwareMap);
            follower.setPose(new Pose(72, 72, 0));
            telemetryM.update(telemetry);
            Drawing.drawCurrent(follower);
            telemetry.update();
        }

        @Override
        public void loop() {
            mechanism.loop();
            mechanism.test(gamepad1, gamepad2, telemetryM, follower);

            follower.update();
            telemetry.addLine(mechanism.toString());
            telemetryM.update(telemetry);
            Drawing.drawCurrent(follower);
            telemetry.update();
        }

        public OpModeMeta meta() {
            return new OpModeMeta.Builder()
                    .setName("TEST: " + mechanismClass.getSimpleName())
                    .setFlavor(OpModeMeta.Flavor.TELEOP)
                    .setGroup("Tests")
                    .setSource(OpModeMeta.Source.BLOCKLY)
                    .build();
        }
    }


    @OpModeRegistrar
    public static void register(OpModeManager manager) {
        if (!Globals.hideTests) {
            GenericTest[] modes = {
                    // TODO: Add more testable mechanisms
                    new GenericTest(TestMechanism.class),
            };

            for (GenericTest mode : modes) {
                manager.register(mode.meta(), mode);
            }
        }
    }
}