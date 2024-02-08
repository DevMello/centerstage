package org.firstinspires.ftc.teamcode.opmode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.button.GamepadButton;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.hardware.lynx.LynxModule;
import org.firstinspires.ftc.robotcore.external.function.Consumer;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.teamcode.subsystem.*;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BaseOpMode extends CommandOpMode {
    protected GamepadEx gamepadEx1, gamepadEx2;
    protected SimpleServo armServo, pitchServo, innerServo, outerServo, stack;
    protected MotorEx leftFront, leftRear, rightRear, rightFront, liftLeft, liftRight, hang, intake;
    protected ArmSys armSys;
    protected BoxSys boxSys;
    protected DriveSys driveSys;
    protected HangSys hangSys;
    protected IntakeSys intakeSys;
    List<LynxModule> hubs;
    @Override
    public void initialize() {
        gamepadEx1 = new GamepadEx(gamepad1);
        gamepadEx2 = new GamepadEx(gamepad2);
        initHardware();
        setupHardware();
        initSubystems();
        setupMisc();
        telemetry.addData("Mode", "Done initializing");
        telemetry.update();
    }

    @Override
    public void run() {
        super.run();
        tad("armState", ArmSys.armState);
        tad("boxState", BoxSys.boxState);
        tad("armServo", armServo.getPosition());
        tad("pitchServo", pitchServo.getPosition());
        tad("innerServo", innerServo.getPosition());
        tad("outerServo", outerServo.getPosition());
        tad("leftFront", leftFront.get());
        tad("leftRear", leftRear.get());
        tad("rightRear", rightRear.get());
        tad("rightFront", rightFront.get());
        telemetry.update();
    }

    public void initHardware() {
        armServo = new SimpleServo(hardwareMap, "armServo", 0, 355);
        pitchServo = new SimpleServo(hardwareMap, "pitchServo", 0, 355);
        innerServo = new SimpleServo(hardwareMap, "innerServo", 0, 255);
        outerServo = new SimpleServo(hardwareMap, "outerServo", 0, 255);
        stack = new SimpleServo(hardwareMap, "stack", 0, 255);
        leftFront = new MotorEx(hardwareMap, "leftFront", Motor.GoBILDA.RPM_435);
        leftRear = new MotorEx(hardwareMap, "leftRear", Motor.GoBILDA.RPM_435);
        rightRear = new MotorEx(hardwareMap, "rightRear", Motor.GoBILDA.RPM_435);
        rightFront = new MotorEx(hardwareMap, "rightFront", Motor.GoBILDA.RPM_435);
        intake = new MotorEx(hardwareMap, "intake", Motor.GoBILDA.RPM_1150);
        liftLeft = new MotorEx(hardwareMap, "lil", Motor.GoBILDA.RPM_1150);
        liftRight = new MotorEx(hardwareMap, "lir", Motor.GoBILDA.RPM_1150);
        hang = new MotorEx(hardwareMap, "hang", Motor.GoBILDA.RPM_30);

    }
    public void setupHardware() {
        leftRear.setInverted(true);
        rightRear.setInverted(true);
    }
    public void initSubystems() {
        armSys = new ArmSys(armServo, pitchServo);
        boxSys = new BoxSys(innerServo, outerServo);
        driveSys = new DriveSys(leftFront, rightFront, leftRear, rightRear);
        hangSys = new HangSys(hang);
        intakeSys = new IntakeSys(stack, intake);
    }
    public void setupMisc() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        final CameraStreamProcessor processor = new CameraStreamProcessor();
//        new VisionPortal.Builder()
//                .addProcessor(processor)
//                .setCamera(hardwareMap.get(WebcamName.class, "webcam"))
//                .build();

        FtcDashboard.getInstance().startCameraStream(processor, 0);
    }

    protected GamepadButton gb1(GamepadKeys.Button button) {
        return gamepadEx1.getGamepadButton(button);
    }
    protected GamepadButton gb2(GamepadKeys.Button button) {
        return gamepadEx2.getGamepadButton(button);
    }
    protected void tad(String caption, Object value) {
        telemetry.addData(caption, value);
    }

    public static class CameraStreamProcessor implements VisionProcessor, CameraStreamSource {
        private final AtomicReference<Bitmap> lastFrame =
                new AtomicReference<>(Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));

        @Override
        public void init(int width, int height, CameraCalibration calibration) {
            lastFrame.set(Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565));
        }

        @Override
        public Object processFrame(Mat frame, long captureTimeNanos) {
            Bitmap b = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(frame, b);
            lastFrame.set(b);
            return null;
        }

        @Override
        public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight,
                                float scaleBmpPxToCanvasPx, float scaleCanvasDensity,
                                Object userContext) {
            // do nothing
        }

        @Override
        public void getFrameBitmap(Continuation<? extends Consumer<Bitmap>> continuation) {
            continuation.dispatch(bitmapConsumer -> bitmapConsumer.accept(lastFrame.get()));
        }
    }
}