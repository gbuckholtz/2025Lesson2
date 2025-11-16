# Stage 04 - Closed-Loop Position Control
Date: 2025-11-16

## Summary
Added precision steering angle control using closed-loop PID position control. Pressing the B button now rotates the steering wheel exactly 90 degrees from its current position.

## New Capability / Rationale
- **Functional Addition**: B button triggers precise 90-degree steering rotation using position feedback
- **Motivation**: Demonstrates the difference between open-loop speed control (Stage 02-03) and closed-loop position control. This is essential for swerve drive systems where wheels must rotate to exact angles for directional control. Position control ensures accuracy regardless of load, friction, or battery voltage.

## Implementation Highlights

### Key Classes Modified
- **`SwerveModule`** (subsystems/SwerveModule.java)
  - Added `PositionVoltage` import and control request object
  - Created `positionRequest` member variable for reusable control requests
  - Added `setSteeringPosition(double rotations)` method:
    - Uses Phoenix 6 `PositionVoltage` control mode
    - Leverages existing PID configuration (kP=10, configured in constructor)
    - Sends position target to motor controller's onboard PID loop

- **`RobotContainer`** (RobotContainer.java)
  - Imported `RotateToAngleCommand`
  - Bound B button: `m_driverController.b().onTrue(new RotateToAngleCommand(m_swerveModule, 0.25))`
  - 0.25 rotations = 90 degrees (360° × 0.25 = 90°)

### New Command Class
- **`RotateToAngleCommand`** (commands/RotateToAngleCommand.java)
  - Constructor parameter: `rotationDelta` (relative rotation amount)
  - `initialize()`: Calculates target position = current + delta, sends position request
  - `execute()`: Continuously sends position request (required for Phoenix 6 control requests)
  - `isFinished()`: Returns true when within tolerance (0.05 rotations = 18°)
  - `end()`: No cleanup needed - PID controller holds position automatically
  - Position tolerance: `POSITION_TOLERANCE = 0.05` rotations

### Control Mode Comparison

| Aspect | Speed Control (Y button) | Position Control (B button) |
|--------|-------------------------|----------------------------|
| **Control Mode** | Open-loop | Closed-loop PID |
| **Motor API** | `steerMotor.set(speed)` | `steerMotor.setControl(positionRequest)` |
| **Precision** | No feedback, speed varies | Feedback-controlled, reaches exact target |
| **Duration** | Continuous while held | Runs until target reached |
| **Use Case** | Manual adjustment | Automated precise movement |

## Testing / Verification

### Build
```bash
./gradlew build
```
Build successful - no compilation errors.

### Deployment Steps
1. Deploy to roboRIO: `./gradlew deploy`
2. Enable robot in teleop mode
3. Note current "Swerve/Angle" value on SmartDashboard
4. Press B button once
5. Observe steering rotate and stop automatically
6. Verify angle increased by ~0.25 rotations (accounting for 12.8:1 gear ratio)

### Expected Behaviors
- **On B press**: Steering rotates smoothly to target position
- **Automatic stop**: Command ends when within 18° of target (0.05 rotation tolerance)
- **Position holding**: Motor maintains position after command ends (PID still active)
- **Repeatability**: Each B press adds another 90° rotation
- **SmartDashboard**: "Swerve/Angle" shows incremental increases of ~0.25

### Interaction with Other Commands
- B button command interrupts Y button manual steering (both require subsystem)
- X button (drive wheel spin) can run simultaneously (different motors)
- After B command completes, Y button manual steering can resume

## Phoenix 6 Position Control Details

### PositionVoltage Control Request
- **Voltage-based**: Uses voltage compensation for consistent performance across battery levels
- **Onboard PID**: Runs on TalonFX motor controller (not roboRIO), reducing latency
- **Must refresh**: Control requests in Phoenix 6 require continuous calls in `execute()`
- **Units**: Position in rotations (after applying gear ratio: 12.8:1)

### PID Configuration Used
From `SwerveModule` constructor:
```java
config.Feedback.SensorToMechanismRatio = 12.8;
config.Slot0.kP = 10;
```
- kP = 10: Proportional gain (higher = faster response, but may oscillate)
- Gear ratio: 12.8:1 converts motor rotations to mechanism rotations
- No kI or kD configured (proportional-only control sufficient for demo)

## Next Steps / Extensions

### Tuning & Safety
- Add timeout safety (prevent infinite waiting if mechanism jammed)
- Tune PID gains (add kI for eliminating steady-state error, kD for damping)
- Add motion profiling for smoother acceleration/deceleration
- Implement soft limits to prevent continuous rotation damage

### Functionality
- Create commands for common angles: 0°, 45°, 90°, 180°
- Add absolute position command (not relative)
- Implement field-centric steering (align to compass direction)
- Create command to return to "home" position (0 degrees)

### User Interface
- Display target vs actual position on SmartDashboard
- Add buttons for clockwise/counterclockwise 90° rotation
- Visualize steering angle with NetworkTables and visualization tools

## Notes

### Position Tolerance Trade-offs
- Current: 0.05 rotations (18°) - fast completion, less precise
- Tighter tolerance (e.g., 0.01 = 3.6°) - more precise, but may never finish if motor oscillates
- Consider different tolerances for different use cases

### Continuous Rotation Consideration
- Position wraps after many rotations (encoder overflow possible)
- For production, consider angle wrapping (e.g., optimize 370° to 10°)
- May need to track absolute position vs relative deltas

### Mechanical Constraints
- Current implementation has no mechanical limits
- Real swerve modules often have 360° rotation with no hard stops
- Wire management may limit practical rotation range
