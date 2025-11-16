# Stage 05 - First Autonomous Command
Date: 2025-11-16

## Summary
Created the first autonomous command that drives the robot forward a specified distance at a controlled speed. The robot will automatically drive forward 30 rotations at 20% speed when autonomous mode is enabled.

## New Capability / Rationale
- **Functional Addition**: Autonomous driving command with configurable distance and speed
- **Motivation**: Introduces autonomous operation - pre-programmed robot behavior without driver input. This is fundamental to FRC competition where robots must complete tasks during a 15-second autonomous period. Demonstrates position-based autonomous control using encoder feedback.

## Implementation Highlights

### New Command Class
- **`DriveDistanceCommand`** (commands/DriveDistanceCommand.java)
  - Extends `Command` base class for autonomous operation
  - Constructor parameters:
    - `swerveModule`: Subsystem to control
    - `rotations`: Target distance in drive motor rotations
    - `speed`: Drive speed from 0.0 to 1.0
  - `initialize()`: Records starting position from encoder
  - `execute()`: Applies constant drive speed
  - `isFinished()`: Returns true when distance traveled >= target
  - `end()`: Stops motor (safety - ensures motor stops even if interrupted)

### Key Classes Modified
- **`RobotContainer`** (RobotContainer.java)
  - Modified `getAutonomousCommand()` to return `DriveDistanceCommand`
  - **Before**: Returned example command from `Autos.exampleAuto()`
  - **After**: Returns `new DriveDistanceCommand(m_swerveModule, 30, 0.2)`
  - Removed unused `Autos` import
  - Updated javadoc to reflect new autonomous behavior

### Autonomous Configuration
- **Distance**: 30 rotations of drive motor
- **Speed**: 0.2 (20% of maximum)
- **Direction**: Forward (positive speed)

## Code Pattern Comparison

### DriveDistanceCommand vs SpinWheelCommand
Both use the same position-tracking pattern:

| Aspect | SpinWheelCommand | DriveDistanceCommand |
|--------|------------------|---------------------|
| **Purpose** | Teleop demo | Autonomous movement |
| **Trigger** | X button press | Autonomous mode start |
| **Distance** | Fixed 10 rotations | Configurable parameter |
| **Speed** | Fixed 0.3 | Configurable parameter |
| **Flexibility** | Single-purpose | Reusable for any distance/speed |

**Key difference**: `DriveDistanceCommand` accepts constructor parameters, making it reusable for different autonomous routines.

## Testing / Verification

### Build
```bash
./gradlew build
```
Build successful - no compilation errors.

### Deployment Steps
1. Deploy to roboRIO: `./gradlew deploy`
2. Connect driver station
3. **Enable autonomous mode** (not teleop)
4. Observe robot drive forward
5. Robot should stop automatically after 30 rotations

### Expected Behaviors
- **Autonomous start**: Motor begins driving at 20% speed immediately
- **During execution**: Constant speed, smooth motion
- **Completion**: Motor stops when encoder reports 30 rotations traveled
- **Safety**: If autonomous is disabled mid-execution, `end()` stops motor
- **SmartDashboard**: Monitor "Swerve/Angle" (steering position remains constant)

### Safety Considerations
- Ensure robot has clear path (30 rotations is significant distance)
- Be ready to disable robot if needed
- Test in open area first
- Lower speed (0.2) provides safer initial testing

## Autonomous Mode in FRC

### How Autonomous Works
1. Driver station signals autonomous period start
2. `Robot.autonomousInit()` calls `getAutonomousCommand()` and schedules it
3. Command runs automatically until finished or autonomous period ends
4. Robot automatically stops when autonomous period ends (15 seconds in competition)

### Autonomous Period Flow
```
Enable Autonomous → autonomousInit() → Schedule Command → 
  initialize() → execute() (loops) → isFinished() = true → 
  end() → Command Complete
```

### Transition to Teleop
- When autonomous ends, scheduled commands are cancelled
- Teleop mode begins - driver control resumes
- Default commands (like joystick drive) automatically restart

## Next Steps / Extensions

### Enhanced Autonomous Routines
- Create command groups to combine multiple actions (drive + turn)
- Add time-based commands (drive for X seconds)
- Implement velocity-based control (instead of fixed speed)
- Use motion profiling for smooth acceleration/deceleration

### Multi-Step Autonomous
```java
return new SequentialCommandGroup(
  new DriveDistanceCommand(m_swerveModule, 30, 0.2),
  new RotateToAngleCommand(m_swerveModule, 0.5),  // 180 degrees
  new DriveDistanceCommand(m_swerveModule, 30, -0.2) // Drive back
);
```

### Safety & Robustness
- Add timeout parameter (max execution time)
- Implement acceleration ramping (don't start at full speed)
- Add velocity feedback (track actual speed vs commanded)
- Integrate gyroscope for straight-line driving

### Configuration
- Move autonomous parameters to `Constants.java`
- Create autonomous chooser (select from multiple routines)
- Add SmartDashboard controls for distance/speed tuning

## Notes

### Encoder-Based Distance
- Distance measured in motor rotations, not wheel rotations
- No drive motor gear ratio configured yet
- Actual robot travel distance depends on wheel diameter and gearing
- For precise distance: `meters = rotations × gear_ratio × wheel_circumference`

### Open-Loop Speed Control
- Current implementation uses open-loop (no velocity feedback)
- Speed varies with battery voltage and load
- For consistent velocity, consider velocity PID control mode

### Single Module Limitation
- Only one swerve module currently implemented
- Real swerve drive requires synchronized control of 4 modules
- This demonstrates the concept with simplified single-module robot

### Autonomous vs Teleop Commands
- Same command class can work in both modes
- `DriveDistanceCommand` could be triggered by button in teleop
- Separation of autonomous routine is just in `getAutonomousCommand()`
