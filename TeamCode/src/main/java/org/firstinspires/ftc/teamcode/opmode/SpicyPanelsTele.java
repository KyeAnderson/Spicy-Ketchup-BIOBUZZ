package org.firstinspires.ftc.teamcode.opmode;

import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.helpers.HoodCompression;
import org.firstinspires.ftc.teamcode.helpers.IntakeVelocity;
import org.firstinspires.ftc.teamcode.helpers.SpicyMode;
import org.firstinspires.ftc.teamcode.helpers.TransferVelocity;
import org.firstinspires.ftc.teamcode.mechanism.Intake;
import org.firstinspires.ftc.teamcode.mechanism.Shooter;
import org.firstinspires.ftc.teamcode.mechanism.Transfer;
import org.firstinspires.ftc.teamcode.pedro.Constants;

public class SpicyPanelsTele extends SpicyMode {
    public SpicyPanelsTele(Globals.MatchSide side) {super(side, Globals.ModeType.TELE);}
    private Follower f;
    private Intake intake;
    private Shooter shooter;
    private Transfer transfer;

    @Override
    public void onRedInit() {
        telemetry.addLine("This will only show up on RED.");
    }

    @Override
    public void onBlueInit() {
        telemetry.addLine("This will only show up on BLUE.");
    }

    @Override
    public void init() {
        super.init();

        f = Constants.create(hardwareMap);
        f.setPose(PoseFactory.degrees().of(56,9,0));
        f.update();
        intake = new Intake(hardwareMap, telemetry);
        shooter = new Shooter(hardwareMap, telemetry);
        transfer = new Transfer(hardwareMap, telemetry);

        telemetry.addData("We're on", side.name());
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
                transfer.setVelocity(TransferVelocity.ON);
            } else {
                transfer.stop();
            }
        } else {
            shooter.stop();
            transfer.stop();
        }
        if(a.right_bumper) {
            intake.setVelocity(IntakeVelocity.ON);
        } else {
            intake.stop();
        }

        if(a.squareWasPressed()) {
            shooter.setCompression(HoodCompression.POLLEN);
        }
        if(a.circleWasPressed()) {
            shooter.setCompression(HoodCompression.NECTAR);
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

    @Override
    public String getOpModeName() {
        return "Panels %s";
    }
}
