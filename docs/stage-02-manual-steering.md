# Stage 02 - Manual Steering Control
Date: 2025-11-16

## Summary
Added ability to manually rotate the swerve module steering angle by holding the Y button. The steering motor spins at a slow, safe speed while the button is held and stops immediately when released.

## New Capability / Rationale
- **Functional Addition**: Y button provides manual steering control - hold to rotate, release to stop
- **Motivation**: Enables driver to manually adjust wheel orientation for testing and demonstration. This allows verification of steering motor operation and provides a foundation for understanding how swerve modules change direction before implementing automated angle control.

## Implementation Highlights

### Key Classes Modified
- **`SwerveModule`** (subsystems/SwerveModule.java)
  - Added `steer(double speed)` method for direct speed control of steering motor
  - Accepts values from -1.0 (full reverse) to 1.0 (full forward)
  - Complements existing `drive(double speed)` method for drive motor

- **`RobotContainer`** (RobotContainer.java)
  - Bound Y button with `whileTrue()` and `onFalse()` pattern
  - While held: runs steering at 0.1 (10%) speed
  - On release: stops steering motor with brief command

### Button Binding Pattern
```java
m_driverController.y()
    .whileTrue(new RunCommand(() -> m_swerveModule.steer(0.1), m_swerveModule))
    .onFalse(new RunCommand(() -> m_swerveModule.steer(0), m_swerveModule).withTimeout(0.02));
```

### Important Constants/Config
- Steering speed: `0.1` (10% of max speed for safe manual control)
- Stop command timeout: `0.02` seconds (20ms - ensures quick stop)

## Testing / Verification

### Build
```bash
./gradlew build
```
Build successful - no compilation errors.

### Deployment Steps
1. Deploy to roboRIO: `./gradlew deploy`
2. Enable robot in teleop mode
3. Press and hold Y button on Xbox controller
4. Observe SmartDashboard "Swerve/Angle" value changing
5. Release Y button - rotation should stop immediately

### Expected Behaviors
- **While Y held**: Steering motor rotates continuously at 10% speed
- **On Y release**: Motor stops immediately
- **SmartDashboard**: "Swerve/Angle" increases while rotating (due to 12.8:1 gear ratio in config)
- **Independence**: Steering control does not interfere with drive motor (left Y stick still controls drive)

### Interaction with Other Commands
- Y button steering and X button spin can be used independently
- Default drive command (left Y joystick) continues to work
- Y button temporarily interrupts default command while held

## Next Steps / Extensions
- Add reverse steering with a different button (B button for negative speed)
- Implement position-based steering commands (e.g., "rotate to 90 degrees")
- Add closed-loop PID control for precise angle targeting
- Create smart dashboard controls to adjust steering speed dynamically
- Add limit switches or soft limits to prevent over-rotation

## Notes
- Steering motor has PID config (`kP = 10`) from initialization but this manual control uses open-loop speed mode
- Gear ratio of 12.8:1 means displayed rotations are mechanism rotations, not motor shaft rotations
- No safety limits implemented - motor will continue rotating indefinitely while button held
- Consider adding current limiting if prolonged rotation causes mechanical stress
