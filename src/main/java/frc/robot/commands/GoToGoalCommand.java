// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.SwerveModule;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

/**
 * Autonomous command that navigates in an S-curve pattern by driving and turning simultaneously.
 * Creates smooth curved motion by running drive and steering commands in parallel:
 * 1. Drive forward 20 rotations WHILE turning clockwise 90 degrees
 * 2. Drive forward 20 rotations WHILE turning counterclockwise 90 degrees
 * 3. Drive forward 20 rotations WHILE turning clockwise 90 degrees
 * 
 * All movements at 10% speed for safe, controlled motion.
 */
public class GoToGoalCommand extends SequentialCommandGroup {
  
  /**
   * Creates a new GoToGoalCommand that executes an S-curve pattern.
   *
   * @param swerveModule The subsystem used by this command.
   */
  public GoToGoalCommand(SwerveModule swerveModule) {
    addCommands(
      // Segment 1: Drive 20 rotations while turning clockwise 90°
      // Drive command is the deadline - turn will be interrupted when drive finishes
      new ParallelDeadlineGroup(
        new DriveDistanceCommand(swerveModule, 20, 0.1),
        new RotateToAngleCommand(swerveModule, 0.25)
      ),
      
      // Segment 2: Drive 20 rotations while turning counterclockwise 90°
      new ParallelDeadlineGroup(
        new DriveDistanceCommand(swerveModule, 20, 0.1),
        new RotateToAngleCommand(swerveModule, -0.25)
      ),
      
      // Segment 3: Drive 20 rotations while turning clockwise 90°
      new ParallelDeadlineGroup(
        new DriveDistanceCommand(swerveModule, 20, 0.1),
        new RotateToAngleCommand(swerveModule, 0.25)
      )
    );
  }
}
