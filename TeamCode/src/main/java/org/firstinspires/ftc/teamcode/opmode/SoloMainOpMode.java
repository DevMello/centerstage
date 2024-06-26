package org.firstinspires.ftc.teamcode.opmode;

import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.ParallelCommandGroup;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystem.LiftSubsystem;

import static com.arcrobotics.ftclib.gamepad.GamepadKeys.Button.*;
import static com.arcrobotics.ftclib.gamepad.GamepadKeys.Button.LEFT_BUMPER;

@TeleOp
public class SoloMainOpMode extends BaseOpMode {
    @Override
    public void initialize() {
        super.initialize();

        register(boxSubsystem, armSubsystem, driveSubsystem, intakeSubsystem, localizerSubsystem, liftSubsystem);

        // plane
//        new GamepadButton(gamepadEx2, LEFT_STICK_BUTTON)
//                .and(new GamepadButton(gamepadEx2, RIGHT_STICK_BUTTON))
//                .and(new GamepadButton(gamepadEx2, A)).whenActive(planeSys.launch());
//        gb1(DPAD_RIGHT).whenPressed(planeSys.launch());

        // arm + box
        gb1(RIGHT_BUMPER).whenPressed(new InstantCommand(() -> boxSubsystem.release()));
//        gb1(LEFT_BUMPER).toggleWhenPressed(
//                new ParallelCommandGroup(armSys.intake(), boxSys.intake()),
//                new ParallelCommandGroup(armSys.deposit(), boxSys.close())
//        );

        // slide + box
        gb1(A).whenPressed(new ParallelCommandGroup(
                liftSubsystem.goTo(LiftSubsystem.NONE), new ParallelCommandGroup(armSubsystem.intake(), boxSubsystem.intake())
        ));
        slideUp(B, LiftSubsystem.LOW);
        slideUp(X, LiftSubsystem.MID);
        slideUp(Y, LiftSubsystem.HIGH);


//        gb2(DPAD_LEFT).toggleWhenPressed(
//                new ParallelCommandGroup(armSys.intake(), boxSys.intake()),
//                new ParallelCommandGroup(armSys.deposit(), boxSys.close())
//        );

        gb1(LEFT_BUMPER).whileHeld(
                driveSubsystem.slow(gamepadEx1::getRightX, gamepadEx1::getLeftY, gamepadEx1::getLeftX));



//        liftSys.setDefaultCommand(liftSys.manualSetHeight(gamepadEx2::getRightY));
        intakeSubsystem.setDefaultCommand(intakeSubsystem.intake(() -> gamepadEx1.gamepad.right_trigger, () -> gamepadEx1.gamepad.left_trigger));
        driveSubsystem.setDefaultCommand(driveSubsystem.drive(gamepadEx1::getRightX, gamepadEx1::getLeftY, gamepadEx1::getLeftX));

    }

    private void slideUp(GamepadKeys.Button button, double height) {
        gb1(button).whenPressed(new ParallelCommandGroup(
                liftSubsystem.goTo(height), new ParallelCommandGroup(armSubsystem.deposit(), boxSubsystem.close())
        ));
    }
}
