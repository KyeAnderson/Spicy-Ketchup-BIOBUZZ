package org.firstinspires.ftc.teamcode.opmode;

import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.mechanism.Hood;
import org.firstinspires.ftc.teamcode.mechanism.Intake;
import org.firstinspires.ftc.teamcode.mechanism.Shooter;
import org.firstinspires.ftc.teamcode.mechanism.Transfer;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@Disabled
@TeleOp(name="Panels gamepad test")
public class SpicyPanelsTele extends OpMode {
    private Follower f;
    private Intake intake;
    private Hood hood;
    private Shooter shooter;
    private Transfer transfer;

    @Override
    public void init() {

        f = Constants.create(hardwareMap);
        f.setPose(PoseFactory.degrees().of(56,9,0));
        f.update();
        intake = new Intake(hardwareMap, telemetry);
        hood = new Hood(hardwareMap, telemetry);
        shooter = new Shooter(hardwareMap, telemetry);
        transfer = new Transfer(hardwareMap, telemetry);

        telemetry.update();
    }

    GamepadManager man = PanelsGamepad.INSTANCE.getFirstManager();
    Gamepad a;

    @Override
    public void loop() {
        /// DRIVE
        a = man.asCombinedFTCGamepad(gamepad1);

        Globals.lastPose = f.pose();

        double forward = -a.left_stick_y;
        double lateral = a.left_stick_x;
        double turn = -a.right_stick_x;

        f.manual(forward,lateral,turn);

        /// CONTROLS

        if(a.right_trigger > 0.7) {
            shooter.setVelocity(Shooter.TESTING_SHOOTER_VELO);
            if(shooter.flywheelAtTarget()) {
                transfer.transfer();
            } else {
                transfer.stop();
            }
        } else {
            shooter.stop();
            transfer.stop();
        }
        if(a.right_bumper) {
            intake.intake();
        } else {
            intake.stop();
        }

        if(a.squareWasPressed()) {
            hood.setupPollen();
        }
        if(a.circleWasPressed()) {
            hood.setupNectar();
        }

        // TELEMETRY / LOOP

        telemetry.addLine("Gamepad1 Right Trigger: Shooter (set velocity to 1870 tps)");
        telemetry.addLine("Gamepad1 Right Bumper: Intake (hold)");
        telemetry.addLine("Gamepad1 Square: Set Hood Compression for Pollen");
        telemetry.addLine("Gamepad1 Circle: Set Hood Compression for Nectar");
        telemetry.addLine(intake.toString());
        telemetry.addLine(shooter.toString());
        telemetry.addLine(transfer.toString());


        intake.loop();
        shooter.loop();
        f.update();
        telemetry.update();
    }
}
