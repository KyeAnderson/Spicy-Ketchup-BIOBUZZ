package org.firstinspires.ftc.teamcode.mechanism;


import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.helpers.ClusterGeometry;
import org.firstinspires.ftc.teamcode.helpers.HardwareConstants;

public class Vision extends Mechanism {
    int TAG_PIPELINE = 0;
    public int RED_PIPELINE = 1;
    public int BLUE_PIPELINE = 2;
    private final Limelight3A limelight;
    private LLResult result;
    private int targetCluster = ClusterGeometry.BLUE_AUDIENCE;
    private double centerTx = Double.NaN;
    private int tagsUsed = 0;
    private final int[] ids = new int[16];
    private final double[] txs = new double[16];

    public Vision(HardwareMap hm, Telemetry tele){
        super(hm, tele);
        limelight = hm.tryGet(Limelight3A.class, HardwareConstants.LIMELIGHT.getId());
        if(limelight!=null) {
            limelight.setPollRateHz(100);
            limelight.pipelineSwitch(TAG_PIPELINE);
            limelight.start();
        }
        displayTelemetry("Connected", () ->(limelight != null && limelight.isConnected()));
        displayTelemetry("Valid result", this::hasResult);
        displayTelemetry("Tags seen", () -> hasResult() ? result.getFiducialResults().size() : 0);
        displayTelemetry("tagsUsed:", ()->tagsUsed);
        displayTelemetry("centerTx:", ()->centerTx);
    }

    public boolean hasResult(){
        return result != null && result.isValid();
    }
    @Override
    public void loop() {
        //establish center asnothing, establish no tags used
        centerTx = Double.NaN;
        tagsUsed = 0;
        if(limelight==null) return;
        result = limelight.getLatestResult();
        if(!hasResult()){ return; }
        /*if((result.getStaleness() + result.getCaptureLatency() + result.getTargetingLatency()) > 100){
            displayTelemetry("Frame too old (>100ms latency):", ()->(result.getStaleness() + result.getCaptureLatency() + result.getTargetingLatency()));
            return;
        }*/


        //fetch latest result
         //if no result, continue

        //loop through each tag in seen results
        for(LLResultTypes.FiducialResult tag : result.getFiducialResults()){

            int id = tag.getFiducialId(); //get ids per tag
            if(ClusterGeometry.clusterOf(id) != targetCluster) continue; //if id is not in the targetted cluster, ignore it
            if(tagsUsed == ids.length) break; //if more tags are seen than allowed, stop

            ids[tagsUsed] = id; //assign tag id in index
            txs[tagsUsed] = tag.getTargetXDegrees(); //assign its tx in index
            tagsUsed++;
        }
        /*if(tagsUsed < 2){

            return;
        }*/
        centerTx = ClusterGeometry.centerTx(ids, txs, tagsUsed); //calculate centerTx based on given tags (min 2)

    }


    @Override
    public void test(Gamepad g1, Gamepad g2, TelemetryManager telemetryM, Follower follower) {
        if(!hasResult() ){
            telemetryM.addLine("No valid result");
            return;
        }
        telemetryM.addData("Pipeline: ", result.getPipelineIndex());
        telemetryM.addData("Latency: ", result.getTargetingLatency()+result.getCaptureLatency());
        for(LLResultTypes.FiducialResult tag : result.getFiducialResults()){
            telemetryM.addLine(String.format("Tag %d tx %.1f ty %.1f", tag.getFiducialId(), tag.getTargetXDegrees(), tag.getTargetYDegrees()));
        }
    }
}