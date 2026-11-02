package org.firstinspires.ftc.teamcode.pedro;

import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector;
import com.pedropathing.paths.Path;

/** * This is the Drawing class. It handles the drawing of stuff on Panels Dashboard, like the robot.
 *
 * @author Lazar - 19234
 * @version 1.1, 5/19/2025
 */
public class Drawing {
    public static final double ROBOT_LENGTH = 18.5; // woah
    public static final double ROBOT_WIDTH = 16.5; // woah
    public static final double ROBOT_RADIUS = Math.sqrt(Math.pow(ROBOT_LENGTH,2) + Math.pow(ROBOT_WIDTH,2)) / 2; // woah
    private static final FieldManager panelsField = PanelsField.INSTANCE.getField();

    public static final Style robotLook = new Style(
            "#FF00FF", "#3F51B5", 1.0
    );
    public static final Style historyLook = new Style(
            "", "#4CAF50", 1.0
    );

    /**
     * This prepares Panels Field for using Pedro Offsets
     */
    public static void init() {
        panelsField.setOffsets(PanelsField.INSTANCE.getPresets().getPEDRO_PATHING());
    }

    /**
     * This draws everything that will be used in the Follower's telemetryDebug() method. This takes
     * a Follower as an input, so an instance of the DashbaordDrawingHandler class is not needed.
     *
     * @param follower Pedro Follower instance.
     */
    public static void drawDebug(Follower follower) {
        if (follower.currentPath() != null) {
            Pose closestPoint = follower.currentPath().get(follower.parametricCompletion());
            drawRobot(new Pose(closestPoint.x(), closestPoint.y(), follower.currentPath().heading(follower.parametricCompletion())), robotLook);
        }
        drawRobot(follower.pose(), historyLook);

        sendPacket();
    }

    public static void drawCurrent(Follower f) {
        try {
            Drawing.drawRobot(f.pose());
            Drawing.sendPacket();
        } catch (Exception e) {
            throw new RuntimeException("Drawing failed " + e);
        }
    }

    /**
     * This draws a robot at a specified Pose with a specified
     * look. The heading is represented as a line.
     *
     * @param pose  the Pose to draw the robot at
     * @param style the parameters used to draw the robot with
     */
    public static void drawRobot(Pose pose, Style style) {
        if (pose == null || Double.isNaN(pose.x()) || Double.isNaN(pose.y()) || Double.isNaN(pose.heading())) {
            return;
        }
        panelsField.setStyle(style);
        panelsField.moveCursor(pose.x(), pose.y());
        panelsField.circle(ROBOT_RADIUS);

        double angle = pose.heading();
        double dirX = Math.cos(angle);
        double dirY = Math.sin(angle);

        double startX = pose.x() + dirX * (ROBOT_RADIUS * 0.3);
        double startY = pose.y() + dirY * (ROBOT_RADIUS * 0.3);
        double endX = pose.x() + dirX * ROBOT_RADIUS;
        double endY = pose.y() + dirY * ROBOT_RADIUS;

        panelsField.moveCursor(startX, startY);
        panelsField.line(endX, endY);

    }

    /**
     * This draws a robot at a specified Pose. The heading is represented as a line.
     *
     * @param pose the Pose to draw the robot at
     */
    public static void drawRobot(Pose pose) {
        drawRobot(pose, robotLook);
    }
    /**
     * This tries to send the current packet to FTControl Panels.
     */
    public static void sendPacket() {
        panelsField.update();
    }
}
