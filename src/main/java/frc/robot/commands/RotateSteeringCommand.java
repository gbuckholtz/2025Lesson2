// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.SwerveModule;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Command that rotates the swerve module steering at a slow speed.
 * Designed to run continuously while a button is held.
 */
public class RotateSteeringCommand extends Command {
  private final SwerveModule m_swerveModule;
  private static final double ROTATION_SPEED = 0.1;

  /**
   * Creates a new RotateSteeringCommand.
   *
   * @param swerveModule The subsystem used by this command.
   */
  public RotateSteeringCommand(SwerveModule swerveModule) {
    m_swerveModule = swerveModule;
    addRequirements(swerveModule);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // No initialization needed - we just start rotating
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_swerveModule.steer(ROTATION_SPEED);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_swerveModule.steer(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    // This command runs indefinitely until interrupted (button release)
    return false;
  }
}
