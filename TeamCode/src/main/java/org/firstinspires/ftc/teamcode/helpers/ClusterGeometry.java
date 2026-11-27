package org.firstinspires.ftc.teamcode.helpers;

public class ClusterGeometry {
    public static final int RED_FAR = 0, RED_AUDIENCE = 1, BLUE_AUDIENCE = 2, BLUE_FAR = 3;
    public static final double[] PER_TAG_OFFSETS_IN = {-6.5, -2.75, 2.75, 6.5};

    public static int clusterOf(int tag){
        return (tag-30)/4;
    } //returns cluster of a tag id
    public static double offsetOf(int tag){
        return PER_TAG_OFFSETS_IN[(tag - 30)%4];
    }

    public static double centerTx(int[] tags, double[] txs, int n){
        if(n < 2) return Double.NaN;
        double offsetSum = 0,txSum = 0, sxy = 0, sxx = 0;

        //xM = mean of offsets
        for(int i = 0; i < n; i++){
            offsetSum += offsetOf(tags[i]);
            txSum+=txs[i];
        }
        double xM = offsetSum / n;
        double yM = txSum / n;

        for(int i = 0; i < n; i++){
            double dx = offsetOf(tags[i]) - xM;
            double dy = txs[i] - yM;
            sxy += dx*dy;
            sxx += dx*dx;
        }
        if(sxx==0) return Double.NaN;

        return yM - (sxy / sxx) * xM;
    }
}
