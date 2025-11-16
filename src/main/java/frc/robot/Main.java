// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;

/**
 * Do not add other code here. Just tell WPILib which robot class to start.
 */
public final class Main {
  private Main() {}

  /**
   * Launch the robot program by giving WPILib the Robot class.
   */
  public static void main(String... args) {
    RobotBase.startRobot(Robot::new);
  }
}
