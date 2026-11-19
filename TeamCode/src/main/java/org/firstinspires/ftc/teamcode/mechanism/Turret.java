package org.firstinspires.ftc.teamcode.mechanism;

import com.arcrobotics.ftclib.controller.PIDController;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.helpers.Globals;
import org.firstinspires.ftc.teamcode.helpers.HardwareConstants;

public class Turret extends Mechanism {
    private final double LEFT_LIMIT = 260; // -90deg, -pi/2, etc
    private final double RIGHT_LIMIT = -260; // 90deg, pi/2, etc
    public static double p = 0.012, i = 0, d = 0.0004, f = 0; // Untuned but worked well
    public final double ticks_in_degrees = (751.8 * 2.75) / 360.0;
    double target;
    DcMotorEx tm;
    public PIDController controller;
    public Turret(HardwareMap hm, Telemetry tele) {
        super(hm, tele);
        tm = hm.get(DcMotorEx.class, HardwareConstants.TURRET_MOTOR.getId());
        tm.setDirection(DcMotor.Direction.FORWARD);
        tm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        tm.setPower(0);
        tm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        tm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        controller = new PIDController(p, i, d);
        
        target = 0;
        controller.setPID(p, i, d);
        displayTelemetry("Target", ()->target);
        displayTelemetry("Current", tm::getCurrentPosition);
    }

    @Override
    public void loop() {
    //   calcPosForRightHive();
    //   calcPosForLeftHive();

        int turretPos = tm.getCurrentPosition();

        double turretPID = controller.calculate(turretPos, target);

        double ff = Math.cos(Math.toRadians(target / this.ticks_in_degrees)) * f;

        double turretPower = turretPID + ff;

        setPower(turretPower);
    }

    public void setTargetAngle(double ang) {
        setTarget(Globals.map(ang, -90, 90, LEFT_LIMIT, RIGHT_LIMIT));
    }
    private void setTarget(double target){
        this.target = target;
    }
    private void setPower(double turretPower){
        tm.setPower(turretPower);
    }
    private void calcPosForRightHive() {
    //    target = 0;
    }
    private void calcPosForLeftHive() {
    //    target = 0;
    }



    double testPos = 0;
    int ci = 0;
    double[] angles = {-90, -45, -35, -15, 0, 15, 35, 45, 90};
    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        if(g1.dpadUpWasPressed()) {
            testPos += 50;
        }
        if(g1.dpadDownWasPressed()) {
            testPos -= 50;
        }
        if (g1.circleWasPressed()){
            setTarget(testPos);
        }
        if(g1.crossWasPressed()) {
            if(ci >= angles.length) ci = 0;
            setTargetAngle(angles[ci++]);
        }
        if(g1.triangle) {
            setTarget(Globals.map(g1.right_stick_x, -1, 1, LEFT_LIMIT, RIGHT_LIMIT));
        }

        telemetryM.addLine("Currently using " + (g1.cross ? "MAX POWER" : "Velocity"));
        telemetryM.addLine("G1 / CIRCLE : Set target to testPos");
        telemetryM.addLine("G1 / DPAD UP : add 50 to testPos");
        telemetryM.addLine("G1 / DPAD DOWN : subtract 50 from testPos");
        telemetryM.addData("testPos", testPos);
    }
}
