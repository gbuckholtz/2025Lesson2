// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.SwerveModule;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Command that rotates the steering wheel to a specific angle relative to current position.
 * Uses closed-loop position control with the steering motor's PID controller.
 */
public class RotateToAngleCommand extends Command {
  private final SwerveModule m_swerveModule;
  private final double m_rotationDelta;
  private double m_targetPosition;
  private static final double POSITION_TOLERANCE = 0.05; // 5% of a rotation

  /**
   * Creates a new RotateToAngleCommand.
   *
   * @param swerveModule The subsystem used by this command.
   * @param rotationDelta The number of rotations to rotate (0.25 = 90 degrees).
   */
  public RotateToAngleCommand(SwerveModule swerveModule, double rotationDelta) {
    m_swerveModule = swerveModule;
    m_rotationDelta = rotationDelta;
    addRequirements(swerveModule);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    double currentPosition = m_swerveModule.getRotations();
    m_targetPosition = currentPosition + m_rotationDelta;
    m_swerveModule.setSteeringPosition(m_targetPosition);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // Position control is handled by the motor controller
    // We just keep sending the position request
    m_swerveModule.setSteeringPosition(m_targetPosition);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // Keep the motor at the final position even after command ends
    // The PID controller will hold it there
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    double currentPosition = m_swerveModule.getRotations();
    return Math.abs(currentPosition - m_targetPosition) < POSITION_TOLERANCE;
  }
}
