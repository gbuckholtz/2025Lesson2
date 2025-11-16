// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.SpinWheelCommand;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.SwerveModule;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class pulls together the main parts of the robot program.
 * It builds the subsystems, controller, and commands so you can see how they connect.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  private final SwerveModule m_swerveModule = new SwerveModule();

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();
  }

  /**
   * Give back the command that runs during the autonomous period.
   * Right now this sends out a simple example command.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return Autos.exampleAuto(m_exampleSubsystem);
  }

  /**
   * Match controller actions to commands.
   * A trigger checks something that can be true or false, like a button press.
   * When a trigger becomes true, the linked command starts running.
   */
  private void configureBindings() {
    m_swerveModule.setDefaultCommand(
        new RunCommand(() -> m_swerveModule.drive(m_driverController.getLeftY()), m_swerveModule));

    // When exampleCondition becomes true, run ExampleCommand once.
    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));

    // When X button is pressed, spin the wheel 10 rotations.
    m_driverController.x().onTrue(new SpinWheelCommand(m_swerveModule));
  }
}
