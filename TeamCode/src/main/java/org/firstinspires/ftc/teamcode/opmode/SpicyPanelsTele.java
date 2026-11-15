package org.firstinspires.ftc.teamcode.opmode;

import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.api.PoseFactory;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.mechanism.Robot;
import org.firstinspires.ftc.teamcode.mechanism.Shooter;

@Disabled
@TeleOp(name="Panels gamepad test")
public class SpicyPanelsTele extends OpMode {
    private Robot robot;

    @Override
    public void init() {

        robot = new Robot(hardwareMap, telemetry);
        robot.follower().setPose(PoseFactory.degrees().of(56,9,0));
        robot.follower().update();

        telemetry.update();
    }

    GamepadManager man = PanelsGamepad.INSTANCE.getFirstManager();
    Gamepad panelsAndNormalGamepad1;

    @Override
    public void loop() {
        /// DRIVE
        panelsAndNormalGamepad1 = man.asCombinedFTCGamepad(gamepad1);

        Globals.lastPose = robot.follower().pose();

        double forward = -panelsAndNormalGamepad1.left_stick_y;
        double lateral = panelsAndNormalGamepad1.left_stick_x;
        double turn = -panelsAndNormalGamepad1.right_stick_x;

        robot.follower().manual(forward,lateral,turn);

        /// CONTROLS

        if(panelsAndNormalGamepad1.right_trigger > 0.7) {
            robot.shooter().setVelocity(Shooter.TESTING_SHOOTER_VELO);
            if(robot.shooter().flywheelAtTarget()) {
                robot.transfer().transfer();
            } else {
                robot.transfer().stop();
            }
        } else {
            robot.shooter().stop();
            robot.transfer().stop();
        }
        if(panelsAndNormalGamepad1.right_bumper) {
            robot.intake().intake();
        } else {
            robot.intake().stop();
        }

        if(panelsAndNormalGamepad1.squareWasPressed()) {
            robot.hood().setupPollen();
        }
        if(panelsAndNormalGamepad1.circleWasPressed()) {
            robot.hood().setupNectar();
        }

        // TELEMETRY / LOOP

        telemetry.addLine("Gamepad1 Right Trigger: Shooter (set velocity to 1870 tps)");
        telemetry.addLine("Gamepad1 Right Bumper: Intake (hold)");
        telemetry.addLine("Gamepad1 Square: Set Hood Compression for Pollen");
        telemetry.addLine("Gamepad1 Circle: Set Hood Compression for Nectar");

        robot.showDebugInfoOnPanels();
        robot.loop();
        telemetry.update();
    }
}
