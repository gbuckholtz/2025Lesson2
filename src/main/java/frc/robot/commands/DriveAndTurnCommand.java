// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.SwerveModule;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Command that simultaneously drives forward and rotates the steering to create arc motion.
 * Combines drive and steering control in a single command to avoid subsystem requirement conflicts.
 */
public class DriveAndTurnCommand extends Command {
  private final SwerveModule m_swerveModule;
  private final double m_driveRotations;
  private final double m_driveSpeed;
  private final double m_steerRotations;
  private double m_startDrivePosition;
  private double m_targetSteerPosition;

  /**
   * Creates a new DriveAndTurnCommand.
   *
   * @param swerveModule The subsystem used by this command.
   * @param driveRotations The number of drive rotations to complete.
   * @param driveSpeed The speed to drive at (0.0 to 1.0).
   * @param steerRotations The number of steering rotations (can be positive or negative).
   */
  public DriveAndTurnCommand(SwerveModule swerveModule, double driveRotations, double driveSpeed, double steerRotations) {
    m_swerveModule = swerveModule;
    m_driveRotations = driveRotations;
    m_driveSpeed = driveSpeed;
    m_steerRotations = steerRotations;
    addRequirements(swerveModule);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_startDrivePosition = m_swerveModule.getDrivePosition();
    double currentSteerPosition = m_swerveModule.getRotations();
    m_targetSteerPosition = currentSteerPosition + m_steerRotations;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // Drive at constant speed
    m_swerveModule.drive(m_driveSpeed);
    
    // Continuously update steering position target
    m_swerveModule.setSteeringPosition(m_targetSteerPosition);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_swerveModule.drive(0);
    // Keep steering at final position - PID will hold it
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    double currentDrivePosition = m_swerveModule.getDrivePosition();
    return (currentDrivePosition - m_startDrivePosition) >= m_driveRotations;
  }
}
