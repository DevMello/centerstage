package org.firstinspires.ftc.teamcode.auto.pathPieces.audienceStart;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.auto.util.AutoBaseOpmode;
import org.firstinspires.ftc.teamcode.roadrunner.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.subsystem.LiftSubsystem;
import android.util.Log;


@Config
@TeleOp (name="To Backdrop Au", group = "ZZZ")
public class StacksToBackdropAu extends AutoBaseOpmode {

    public enum State{
        WAIT_FOR_START,
        MOVE_TO_DETECT_POS,
        CHECK_FOR_BOT,
        MOVE_TO_BACKDROP,
        DEPOSIT,
        WAIT_FOR_FINISH,
        FINISHED
    }
    public static State currentState = State.WAIT_FOR_START;

    public static boolean red = true;
    public static int zone = 1;

    public static double botDetectionThreshold = 65;
    public static double depositWaitTime = 500;
    public static double pixelX = -56;
    public static double pixelY = 36;
    public static double trussPixelSideX = -30;
    public static double trussPixelSideY = 60;
    public static double bottomPathY = 60;
    public static double lineToX = 24;
    public static double lineToY = 57;
    public static double waitingX = 34;
    public static double waitingY = 53;
    public static double waitingHeading = 75;
    public static double backdropX = 50;
    public static double backdropY1 = 29;
    public static double backdropY2 = 36;
    public static double backdropY3 = 44;

    public static boolean testing = false;

    private ElapsedTime depositWaitTimer;

    @Override
    public void init(){
        super.init();
        depositWaitTimer = new ElapsedTime();

        //TODO: remove setPosEstimate
        if(red)
            drive.setPoseEstimate(new Pose2d(pixelX+3.5, -pixelY, Math.toRadians(180)));
        else
            drive.setPoseEstimate(new Pose2d(pixelX+3.5, pixelY, Math.toRadians(180)));
    }

    @Override
    public void loop() {
        super.loop();
        drive.update();

        if(gamepad1.right_bumper && !testing){
            testing = true;
            currentState = State.MOVE_TO_DETECT_POS;
        }
        if(currentState == State.MOVE_TO_DETECT_POS){
            schedule(boxSubsystem.close());
            if(red){
                drive.followTrajectorySequenceAsync(drive.trajectorySequenceBuilder(drive.getPoseEstimate())
                        .splineToConstantHeading(new Vector2d(trussPixelSideX, -bottomPathY), Math.toRadians(0))
                        .splineTo(new Vector2d(lineToX, -bottomPathY), Math.toRadians(0.00))
                        .lineToSplineHeading(new Pose2d(waitingX, -bottomPathY, Math.toRadians(180+waitingHeading)),
                                SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                                SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                        .waitSeconds(0.5)
                        .build());
            }else{
                drive.followTrajectorySequenceAsync(drive.trajectorySequenceBuilder(drive.getPoseEstimate())
                        .splineToConstantHeading(new Vector2d(trussPixelSideX, bottomPathY), Math.toRadians(0))
                        .splineTo(new Vector2d(lineToX, bottomPathY), Math.toRadians(0.00))
                        .lineToSplineHeading(new Pose2d(waitingX, bottomPathY, Math.toRadians(180-waitingHeading)),
                                SampleMecanumDrive.getVelocityConstraint(10, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                                SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                        .waitSeconds(0.5)
                        .build());
            }
            currentState = State.CHECK_FOR_BOT;
        }
        if(currentState == State.CHECK_FOR_BOT && !drive.isBusy()){
            while(true){
                Log.d("asd",""+ localizerSubsystem.getBl());
            }
//            if(localizerSys.getBl() > botDetectionThreshold){
//                currentState = State.MOVE_TO_BACKDROP;
//            }
        }
        if(currentState == State.MOVE_TO_BACKDROP){
            schedule(liftSubsystem.goTo(LiftSubsystem.LOW));
            schedule(armSubsystem.deposit());
            if(red){
                switch(zone){
                    case 1:
                        drive.followTrajectorySequenceAsync(drive.trajectorySequenceBuilder(new Pose2d(waitingX, -bottomPathY, Math.toRadians(180+waitingHeading)))
                                .lineToLinearHeading(new Pose2d(backdropX, -backdropY1, Math.toRadians(180)))
                                .build());
                        break;
                    case 2:
                        drive.followTrajectorySequenceAsync(drive.trajectorySequenceBuilder(new Pose2d(waitingX, -bottomPathY, Math.toRadians(180+waitingHeading)))
                                .lineToLinearHeading(new Pose2d(backdropX, -backdropY2, Math.toRadians(180)))
                                .build());
                        break;
                    case 3:
                        drive.followTrajectorySequenceAsync(drive.trajectorySequenceBuilder(new Pose2d(waitingX, -bottomPathY, Math.toRadians(180+waitingHeading)))
                                .lineToLinearHeading(new Pose2d(backdropX, -backdropY3, Math.toRadians(180)))
                                .build());
                        break;
                }
            }else{
                switch(zone){
                    case 1:
                        drive.followTrajectorySequenceAsync(drive.trajectorySequenceBuilder(new Pose2d(waitingX, bottomPathY, Math.toRadians(180-waitingHeading)))
                                .lineToLinearHeading(new Pose2d(backdropX, backdropY3, Math.toRadians(180)))
                                .build());
                        break;
                    case 2:
                        drive.followTrajectorySequenceAsync(drive.trajectorySequenceBuilder(new Pose2d(waitingX, bottomPathY, Math.toRadians(180-waitingHeading)))
                                .lineToLinearHeading(new Pose2d(backdropX, backdropY2, Math.toRadians(180)))
                                .build());
                        break;
                    case 3:
                        drive.followTrajectorySequenceAsync(drive.trajectorySequenceBuilder(new Pose2d(waitingX, bottomPathY, Math.toRadians(180-waitingHeading)))
                                .lineToLinearHeading(new Pose2d(backdropX, backdropY1, Math.toRadians(180)))
                                .build());
                        break;
                }
            }
            currentState = State.DEPOSIT;
        }
        if(currentState == State.DEPOSIT && !drive.isBusy()){
            schedule(boxSubsystem.intake());
            depositWaitTimer.reset();
            //TODO: add apriltag relocalization
            currentState = State.WAIT_FOR_FINISH;
        }
        if(currentState == State.WAIT_FOR_FINISH && depositWaitTimer.milliseconds() > depositWaitTime){
            testing = false;
            currentState = State.FINISHED;
        }
    }

    public static void run(int detectedZone, boolean redSide){
        zone = detectedZone;
        red = redSide;
        currentState = State.MOVE_TO_DETECT_POS;
    }
    public static boolean isFinished(){
        return currentState == State.FINISHED;
    }
}
