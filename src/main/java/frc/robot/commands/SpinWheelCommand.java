// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.SwerveModule;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Command that spins the swerve module drive wheel 10 rotations.
 */
public class SpinWheelCommand extends Command {
  private final SwerveModule m_swerveModule;
  private double m_startPosition;
  private static final double ROTATIONS_TO_SPIN = 10.0;
  private static final double SPIN_SPEED = 0.3;

  /**
   * Creates a new SpinWheelCommand.
   *
   * @param swerveModule The subsystem used by this command.
   */
  public SpinWheelCommand(SwerveModule swerveModule) {
    m_swerveModule = swerveModule;
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
    m_swerveModule.drive(SPIN_SPEED);
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
    return (currentPosition - m_startPosition) >= ROTATIONS_TO_SPIN;
  }
}
