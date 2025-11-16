// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ExampleSubsystem extends SubsystemBase {
  /** Basic subsystem template with a sample command and sensor check. */
  public ExampleSubsystem() {}

  /**
   * Return a one-time command that would run a short action for this subsystem.
   */
  public Command exampleMethodCommand() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    return runOnce(
        () -> {
          /* one-time action goes here */
        });
  }

  /**
   * Return a fake sensor value for showing how trigger conditions can work.
   */
  public boolean exampleCondition() {
    // Query some boolean state, such as a digital sensor.
    return false;
  }

  @Override
  public void periodic() {
    // Code here would run every robot cycle when this subsystem is alive.
  }

  @Override
  public void simulationPeriodic() {
    // Code here would run each cycle while simulating the robot.
  }
}
