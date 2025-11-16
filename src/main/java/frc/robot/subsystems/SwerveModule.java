// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Rotation;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SwerveModule extends SubsystemBase {

  
  TalonFX driveMotor = new TalonFX(7);
  TalonFX steerMotor = new TalonFX(8);

  /** Creates a new SwerveModule. */
  public SwerveModule() {
    
    
    TalonFXConfiguration config = new TalonFXConfiguration();
    config.Feedback.SensorToMechanismRatio = 12.8;

    config.Slot0.kP = 10;

    steerMotor.getConfigurator().apply(config);
    steerMotor.setPosition(0);
  }

  /**
   * 
   * @param speed Speed from -1 to 1
   */
  public void drive(double speed){
    driveMotor.set(speed);
  }

  public void  rotate(double angle)
  {
       steerMotor.set(angle);
  }

  public void setHeading(double rotation) {
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Swerve/Angle", getRotations());
  }

  public double getRotations(){
    return steerMotor.getPosition().getValue().in(Rotation);
  }
}
