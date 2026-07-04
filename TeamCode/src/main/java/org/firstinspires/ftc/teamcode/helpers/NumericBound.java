package org.firstinspires.ftc.teamcode.helpers;

import androidx.annotation.NonNull;

public class NumericBound {
    public double upper;
    public double lower;
    public NumericBound(double lower, double upper) {
        this.upper = upper;
        this.lower = lower;
    }
    public boolean inRange(double check) {
        return (check >= lower) && (check <= upper);
    }
    public double error(double check) {
        if (check < lower) {
            return check - lower;
        } else if (check > upper) {
            return check - upper;
        } else {
            return 0;
        }
    }

    public double clip(double number) {
        return Math.max(lower, Math.min(upper, number));
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + lower +"," + upper+"]";
    }
}