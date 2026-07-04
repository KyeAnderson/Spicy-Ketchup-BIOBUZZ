package org.firstinspires.ftc.teamcode.commandeer.logical;

import com.pedropathing.util.Timer;

import org.firstinspires.ftc.teamcode.commandeer.CCommand;
import org.firstinspires.ftc.teamcode.commandeer.CState;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

public class WaitUntil extends CCommand {
    public WaitUntil(BooleanSupplier condition) {
        super(()->condition.getAsBoolean() ? CState.WORKING : CState.END);
    }
}