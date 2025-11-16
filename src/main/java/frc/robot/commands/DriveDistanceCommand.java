// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.SwerveModule;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Autonomous command that drives the robot forward a specified distance.
 * Uses the drive motor position to track progress.
 */
public class DriveDistanceCommand extends Command {
  private final SwerveModule m_swerveModule;
  private final double m_rotations;
  private final double m_speed;
  private double m_startPosition;

  /**
   * Creates a new DriveDistanceCommand.
   *
   * @param swerveModule The subsystem used by this command.
   * @param rotations The number of rotations to drive.
   * @param speed The speed to drive at (0.0 to 1.0).
   */
  public DriveDistanceCommand(SwerveModule swerveModule, double rotations, double speed) {
    m_swerveModule = swerveModule;
    m_rotations = rotations;
    m_speed = speed;
    addRequirements(swerveModule);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_startPosition = m_swerveModule.getDrivePosition();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_swerveModule.drive(m_speed);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_swerveModule.drive(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    double currentPosition = m_swerveModule.getDrivePosition();
    return (currentPosition - m_startPosition) >= m_rotations;
  }
}
