package org.firstinspires.ftc.teamcode.helpers;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.teamcode.opmode.SpicyPanelsTele;
import org.firstinspires.ftc.teamcode.opmode.auto.AutoUno;
import org.firstinspires.ftc.teamcode.opmode.auto.Match2Auto;

import java.util.HashSet;
import java.util.Set;

public class Globals {
    public static Pose lastPose;
    public static Pose redBasketAudienceGoal = new Pose(58, 54);
    public static final boolean hideTests = false;
    public static double map(double value, double inMin, double inMax, double outMin, double outMax) {
        double inRange = inMax - inMin;
        if (inRange == 0.0) {
            throw new IllegalArgumentException("Input range cannot be zero.");
        }
        if(value > inMax) value = inMax;
        if(value < inMin) value = inMin;

        return outMin + ((value - inMin) / inRange) * (outMax - outMin);
    }
}
