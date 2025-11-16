# Stage 01 - Spin Wheel on Button Press
Date: 2025-11-16

## Summary
Added capability to spin the swerve module drive wheel 10 rotations when the driver presses the X button on the Xbox controller.

## New Capability / Rationale
- **Functional Addition**: X button triggers automated 10-rotation wheel spin
- **Motivation**: Demonstrates basic command binding and position-based motor control using Phoenix 6 TalonFX motors. This establishes the foundation for more complex autonomous movements.

## Implementation Highlights

### Key Classes Modified
- **`SwerveModule`** (subsystems/SwerveModule.java)
  - Added `getDrivePosition()` method to track drive motor rotations
  - Returns position in rotations using Phoenix 6 Units API

- **`RobotContainer`** (RobotContainer.java)
  - Imported `SpinWheelCommand`
  - Bound X button to trigger `SpinWheelCommand`: `m_driverController.x().onTrue(new SpinWheelCommand(m_swerveModule))`

### New Commands
- **`SpinWheelCommand`** (commands/SpinWheelCommand.java)
  - Position-based command that runs until drive motor completes 10 rotations
  - Drives at 0.3 (30%) speed during execution
  - Tracks starting position in `initialize()`
  - Monitors delta in `isFinished()` - ends when `(current - start) >= 10.0`
  - Stops motor in `end()` - default command resumes afterward

### Important Constants/Config
- `ROTATIONS_TO_SPIN = 10.0` - target rotation count
- `SPIN_SPEED = 0.3` - drive speed during spin (30% output)

## Testing / Verification

### Build
```bash
./gradlew build
```
Build successful - no compilation errors.

### Deployment Steps
1. Deploy to roboRIO: `./gradlew deploy`
2. Enable robot in teleop mode
3. Press X button on Xbox controller

### Expected Behaviors
- Wheel spins forward at moderate speed
- Command runs until 10 rotations complete (measured from drive motor encoder)
- Motor stops automatically when target reached
- Default joystick drive control resumes

### Telemetry to Monitor
- SmartDashboard: "Swerve/Angle" shows steering position (existing)
- Consider adding drive position to dashboard for debugging

## Next Steps / Extensions
- Add SmartDashboard output for drive motor position
- Create commands for precise distance movements
- Implement full swerve drive with all 4 modules
- Add joystick button to reverse spin direction
- Make rotation count and speed configurable via Constants

## Notes
- Command uses requires() so it interrupts default drive command while running
- Drive motor has no gear ratio configured yet - position readings are in motor rotations, not wheel rotations
- For production, consider adding timeout safety in case motor doesn't reach target
