# Stage 03 - Refactor to Command Class Pattern
Date: 2025-11-16

## Summary
Refactored the Y button steering control from inline lambda functions to a dedicated `RotateSteeringCommand` class, demonstrating an alternative command implementation pattern.

## New Capability / Rationale
- **Functional Addition**: No change in functionality - same steering behavior
- **Motivation**: Demonstrates two approaches to implementing commands in WPILib:
  1. **Stage 02 approach**: Inline lambdas with `RunCommand` - quick and concise
  2. **Stage 03 approach**: Dedicated command class - better organization and testability
  
This refactor shows when to use each pattern and provides a template for creating custom command classes.

## Implementation Highlights

### New Command Class
- **`RotateSteeringCommand`** (commands/RotateSteeringCommand.java)
  - Extends `Command` base class
  - Encapsulates steering rotation logic in proper lifecycle methods
  - `initialize()`: No setup needed (could be used for starting position tracking)
  - `execute()`: Calls `m_swerveModule.steer(0.1)` every scheduler cycle
  - `end(boolean interrupted)`: Stops motor by calling `m_swerveModule.steer(0)`
  - `isFinished()`: Returns `false` - runs until button released
  - Speed constant: `ROTATION_SPEED = 0.1`

### Key Classes Modified
- **`RobotContainer`** (RobotContainer.java)
  - Simplified Y button binding from 3 lines to 1:
    - **Before**: `.whileTrue(lambda).onFalse(lambda with timeout)`
    - **After**: `.whileTrue(new RotateSteeringCommand(m_swerveModule))`
  - Added import for `RotateSteeringCommand`
  - Removed dependency on creating separate stop command

### Code Comparison

**Lambda approach (Stage 02):**
```java
m_driverController.y()
    .whileTrue(new RunCommand(() -> m_swerveModule.steer(0.1), m_swerveModule))
    .onFalse(new RunCommand(() -> m_swerveModule.steer(0), m_swerveModule).withTimeout(0.02));
```

**Command class approach (Stage 03):**
```java
m_driverController.y().whileTrue(new RotateSteeringCommand(m_swerveModule));
```

## Testing / Verification

### Build
```bash
./gradlew build
```
Build successful - no compilation errors.

### Deployment Steps
Same testing procedure as Stage 02:
1. Deploy to roboRIO: `./gradlew deploy`
2. Enable robot in teleop mode
3. Press and hold Y button
4. Verify steering rotates at same speed as before
5. Release Y - motor should stop

### Expected Behaviors
Identical to Stage 02:
- Steering rotates at 10% speed while Y held
- Stops immediately on release
- SmartDashboard "Swerve/Angle" updates

## Architectural Benefits

### When to Use Command Classes (vs Lambdas)
**Use dedicated command class when:**
- Logic is more than 1-2 lines
- Need to track state (like `SpinWheelCommand` tracking start position)
- Want to reuse command in multiple places
- Need unit testing
- Command may grow in complexity

**Use lambda/RunCommand when:**
- Simple one-liner logic
- Used only once
- No state tracking needed
- Prototype/testing phase

### Command Lifecycle Advantages
- `end()` method automatically called on interrupt or button release
- No need for separate `.onFalse()` binding
- Clearer separation of start/run/stop logic
- Easier to add telemetry, logging, or safety checks

## Next Steps / Extensions
- Add constructor parameter for configurable speed
- Create reverse rotation command (negative speed)
- Add position tracking to limit rotation range
- Implement `SmartDashboard` controls to tune speed at runtime
- Create command group combining drive and steering

## Notes
- Both patterns (lambda and command class) are valid WPILib approaches
- `whileTrue()` automatically cancels command when trigger becomes false
- Command's `end()` method is more reliable than separate `.onFalse()` for cleanup
- This pattern matches `SpinWheelCommand` structure for consistency
