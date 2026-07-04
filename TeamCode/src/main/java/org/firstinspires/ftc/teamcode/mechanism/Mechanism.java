package org.firstinspires.ftc.teamcode.mechanism;

import androidx.annotation.NonNull;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class Mechanism {
    public HardwareMap hardwareMap;
    public Telemetry telemetry;
    public static Mechanism INSTANCE;
    private final LinkedHashMap<String, Supplier<String>> selfTelemetry;

    public Mechanism(HardwareMap hm, Telemetry tele) {
        this.hardwareMap = hm;
        this.telemetry = tele;
        selfTelemetry = new LinkedHashMap<>();
        INSTANCE = this;
    }

    public void displayTelemetry(String identifier, String line) {
        selfTelemetry.put(identifier, () -> line);
    }

    public void displayTelemetry(String identifier, Supplier<?> line) {
        selfTelemetry.put(identifier, () -> String.valueOf(line.get()));
    }

    /**
     * Reset a motor by setting its power to 0, resetting the encoder, and restarting its run-mode.
     * @param encoderEnabled Should it run using encoders?
     * @param motors List of motors to reset
     */
    public static void resetMotor(boolean encoderEnabled, DcMotor ...motors) {
        for(DcMotor mot : motors) {
            mot.setPower(0);
            mot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            mot.setMode(encoderEnabled ? DcMotor.RunMode.RUN_USING_ENCODER : DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    /**
     * Initialize by resetting all supplied motors with encoder enabled by default.
     * @param motors List of motors to initialize.
     */
    public static void initializeMotors(DcMotor ...motors) {
        Mechanism.resetMotor(true, motors);
        for(DcMotor mot : motors) mot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public abstract void loop();
    public abstract void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower);

    @NonNull
    public String toString() {
        StringBuilder out = new StringBuilder(getClass().getSimpleName() + " >\n");
        for(Map.Entry<String, Supplier<String>> entry : selfTelemetry.entrySet()) {
            out.append(" ] ").append(entry.getKey()).append(": ").append(entry.getValue().get()).append("\n");
        }
        return out.toString();
    }
}