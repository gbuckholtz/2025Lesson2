// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;


import frc.robot.subsystems.SwerveModule;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Command that keeps the steering motor turning while it is scheduled.
 */
public class RotateMotorCommand extends Command {
  
  SwerveModule m_SwerveModule;
  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public RotateMotorCommand(SwerveModule subsystem) {
    m_SwerveModule = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // Spin the steering motor slowly in the positive direction.
    m_SwerveModule.rotate(.1);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_SwerveModule.rotate(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
