// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Rotation;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Subsystem for one swerve wheel. It controls the drive motor and the steering motor.
 */
public class SwerveModule extends SubsystemBase {

  
  TalonFX driveMotor = new TalonFX(7);
  TalonFX steerMotor = new TalonFX(8);

  /** Set up the motors and their sensors. */
  public SwerveModule() {
    
    
    TalonFXConfiguration config = new TalonFXConfiguration();
    config.Feedback.SensorToMechanismRatio = 12.8;

    config.Slot0.kP = 10;

    steerMotor.getConfigurator().apply(config);
    steerMotor.setPosition(0);
  }

  /**
   * Drive the wheel forward or backward with a value from -1 (full reverse) to 1 (full forward).
   */
  public void drive(double speed){
    driveMotor.set(speed);
  }

  /**
   * Turn the wheel by sending a position request to the steering motor.
   */
    // Removed rotate(angle) method; steering rotation command no longer used.

  public void setHeading(double rotation) {
  }

  @Override
  public void periodic() {
    // Show the steering position on the SmartDashboard to help with debugging.
    SmartDashboard.putNumber("Swerve/Angle", getRotations());
  }

  public double getRotations(){
    return steerMotor.getPosition().getValue().in(Rotation);
  }

  /**
   * Get the current position of the drive motor in rotations.
   */
  public double getDrivePosition() {
    return driveMotor.getPosition().getValue().in(Rotation);
  }
}
