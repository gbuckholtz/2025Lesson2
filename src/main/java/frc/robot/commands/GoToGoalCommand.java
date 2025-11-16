// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.SwerveModule;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

/**
 * Autonomous command that navigates through a complex path:
 * 1. Drive forward 20 rotations
 * 2. Turn clockwise 90 degrees (0.25 rotations)
 * 3. Drive forward 20 rotations
 * 4. Turn counterclockwise 90 degrees (-0.25 rotations)
 * 5. Drive forward 20 rotations
 * 
 * All movements at 10% speed for safe, controlled motion.
 */
public class GoToGoalCommand extends SequentialCommandGroup {
  
  /**
   * Creates a new GoToGoalCommand.
   *
   * @param swerveModule The subsystem used by this command.
   */
  public GoToGoalCommand(SwerveModule swerveModule) {
    addCommands(
      // Step 1: Drive forward 20 rotations at 10% speed
      new DriveDistanceCommand(swerveModule, 20, 0.1),
      
      // Step 2: Turn clockwise 90 degrees (0.25 rotations)
      new RotateToAngleCommand(swerveModule, 0.25),
      
      // Step 3: Drive forward 20 rotations at 10% speed
      new DriveDistanceCommand(swerveModule, 20, 0.1),
      
      // Step 4: Turn counterclockwise 90 degrees (-0.25 rotations)
      new RotateToAngleCommand(swerveModule, -0.25),
      
      // Step 5: Drive forward 20 rotations at 10% speed
      new DriveDistanceCommand(swerveModule, 20, 0.1)
    );
  }
}
