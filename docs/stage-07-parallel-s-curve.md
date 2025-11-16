# Stage 07 - S-Curve Motion with Combined Drive and Turn
Date: 2025-11-16

## Summary
Created `DriveAndTurnCommand` to control both drive and steering motors simultaneously within a single command, enabling smooth S-curve motion. Refactored `GoToGoalCommand` to use this combined command instead of parallel command groups.

## New Capability / Rationale
- **Functional Addition**: Simultaneous drive and steering for arc/curve motion
- **Motivation**: Demonstrates how to coordinate multiple motors within a single command to create natural, flowing robot motion. Initially attempted using `ParallelDeadlineGroup`, but discovered WPILib's fundamental rule: **multiple commands in a parallel group cannot require the same subsystem**. This stage shows the correct pattern: combine motor control within one command when they operate on the same subsystem.

## Implementation Highlights

### New Command Class
- **`DriveAndTurnCommand`** (commands/DriveAndTurnCommand.java)
  - Single command controlling both drive and steering motors
  - Constructor parameters:
    - `driveRotations`: Distance to travel
    - `driveSpeed`: Drive motor speed (0.0 to 1.0)
    - `steerRotations`: Steering angle change (positive = clockwise)
  - `initialize()`: Records start positions, calculates steering target
  - `execute()`: Simultaneously controls both motors:
    - `drive(speed)` - constant speed control
    - `setSteeringPosition(target)` - PID position control
  - `isFinished()`: Returns true when drive distance reached
  - `end()`: Stops drive motor, leaves steering at final position

### Key Classes Modified
- **`GoToGoalCommand`** (commands/GoToGoalCommand.java)
  - Removed `ParallelDeadlineGroup` import
  - Changed from parallel groups to sequential `DriveAndTurnCommand` instances
  - Each segment now uses single combined command
  - Simpler, cleaner structure

### Command Structure

**Final Implementation (Sequential Combined Commands):**
```java
addCommands(
  new DriveAndTurnCommand(swerveModule, 20, 0.1, 0.25),   // Arc right
  new DriveAndTurnCommand(swerveModule, 20, 0.1, -0.25),  // Arc left
  new DriveAndTurnCommand(swerveModule, 20, 0.1, 0.25)    // Arc right
);
```

## Motion Pattern Analysis

### Stage 06 Path (Sequential - Stop Between Actions)
```
Start →→→→→→→→→→→ (straight)
                  └─┐ (stop, rotate in place)
                    →→→→→→→→→→→ (straight)
                               ┌─┘ (stop, rotate in place)
                    ←←←←←←←←←←← (straight)
```
- Sharp corners, stop between actions
- Predictable but mechanical motion

### Stage 07 Path (Combined Control - Simultaneous)
```
Start ~~~~~~~~> (smooth curve right)
       ~~~~~~~~> (smooth curve left)
                ~~~~~~~~> (smooth curve right)
```
- Flowing S-pattern, no stops
- More natural, efficient motion
- Both motors controlled in same command

### Curvature Characteristics
- **First segment**: Curves clockwise while moving forward
- **Second segment**: Curves counterclockwise (reverses curvature) → creates S-shape
- **Third segment**: Curves clockwise again
- **Arc radius**: Determined by ratio of drive speed to steering rotation rate

## WPILib Subsystem Requirements Rule

### The Fundamental Constraint
**Rule**: Multiple commands in a parallel group cannot require the same subsystem.

**Why This Error Occurred:**
```java
// Both commands require m_swerveModule
new ParallelDeadlineGroup(
  new DriveDistanceCommand(swerveModule, ...),  // addRequirements(swerveModule)
  new RotateToAngleCommand(swerveModule, ...)   // addRequirements(swerveModule)
)
// ERROR: java.lang.IllegalArgumentException
```

**WPILib's Reasoning:**
- Subsystems enforce mutual exclusion
- Only one command can "own" a subsystem at a time
- Prevents conflicting control of the same hardware
- Scheduler cannot resolve which command should control subsystem

### Solution Patterns

**Pattern 1: Combine into Single Command (Used Here)**
```java
// One command controls both motors
public class DriveAndTurnCommand extends Command {
  public void execute() {
    m_swerveModule.drive(speed);           // Drive motor
    m_swerveModule.setSteeringPosition(angle);  // Steer motor
  }
}
```
✅ Single subsystem requirement
✅ Full control over both motors
✅ Clean execution logic

**Pattern 2: Split Subsystems (Alternative)**
```java
// Separate subsystems for drive and steer
new ParallelDeadlineGroup(
  new DriveDistanceCommand(driveSubsystem, ...),
  new RotateToAngleCommand(steerSubsystem, ...)
)
```
✅ Different subsystems can run in parallel
⚠️ Requires architectural change
⚠️ More complex subsystem structure

**Pattern 3: Sequential Execution (Stage 06)**
```java
// Commands run one after another
new SequentialCommandGroup(
  new DriveDistanceCommand(...),
  new RotateToAngleCommand(...)
)
```
✅ No subsystem conflicts
❌ No simultaneous motion
❌ Less efficient

## Testing / Verification

### Build
```bash
./gradlew build
```
Build successful - no compilation errors.

### Deployment Steps
1. Deploy to roboRIO: `./gradlew deploy`
2. Enable autonomous mode
3. **Ensure large open space** - path will be curved
4. Observe smooth S-curve motion
5. Both motors working simultaneously throughout each segment

### Expected Behaviors
- **Autonomous start**: Immediately begins driving AND turning
- **Motion quality**: Smooth, continuous - no stops between segments
- **Path shape**: S-curve pattern (right-left-right curvature)
- **Completion**: Stops after third segment finishes
- **Transitions**: Brief pause between segments as commands change

### Observation Points
- **SmartDashboard "Swerve/Angle"**: Continuously changing during each segment
- **Drive position**: Steadily increasing (no stops within segments)
- **Steering motion**: Smooth rotation to each target angle
- **Final position**: Robot ends at different location and heading than start

### Error Resolution
**Initial Error:**
```
java.lang.IllegalArgumentException: Multiple commands in a parallel group 
cannot require the same subsystems
```

**Fix:** Created `DriveAndTurnCommand` to combine both motor controls in a single command instead of using `ParallelDeadlineGroup` with separate commands.

## Architectural Patterns

### Single Command Controlling Multiple Motors
The key insight: When multiple motors need to work together on the same subsystem, combine their control in one command's `execute()` method:

```java
@Override
public void execute() {
  // Control both motors simultaneously
  m_swerveModule.drive(m_driveSpeed);              // Motor 1
  m_swerveModule.setSteeringPosition(m_targetSteerPosition);  // Motor 2
}
```

**Benefits:**
- ✅ Single subsystem requirement (no conflicts)
- ✅ Synchronized control (both motors in same cycle)
- ✅ Shared state (can coordinate based on both motor states)
- ✅ Clean lifecycle (initialize/end handle both motors together)

### Why Parallel Groups Don't Work Here
Parallel command groups are designed for **independent subsystems**:
- ✅ Good: Drive subsystem + Intake subsystem
- ✅ Good: Shooter subsystem + Vision subsystem
- ❌ Bad: Same subsystem, different motors (use combined command)

### When to Use Each Pattern

**Combined Command (`DriveAndTurnCommand`):**
- Motors are part of same subsystem
- Need tight coordination between motors
- Motors work toward single unified goal
- Example: Swerve module (drive + steer)

**Parallel Command Groups:**
- Separate, independent subsystems
- Concurrent but uncoordinated actions
- Example: Drive while running intake

**Sequential Command Groups:**
- One action must complete before next begins
- Example: Drive to position, then shoot

## Next Steps / Extensions

### Motion Refinement
- Tune drive/turn parameters for desired curve radius
- Add velocity ramping for smoother acceleration
- Implement closed-loop curve following (feedback control)
- Calculate arc radius: `radius = drive_distance / steering_angle`

### More Complex Combined Commands
```java
// Drive while tracking a target with vision
public class DriveToTargetCommand extends Command {
  public void execute() {
    m_swerveModule.drive(0.3);                    // Forward motion
    double angle = m_vision.getTargetAngle();     // Get target bearing
    m_swerveModule.setSteeringPosition(angle);    // Auto-aim while driving
  }
}
```

### Multi-Subsystem Parallel Operations
```java
// Now that we know the rules, we can do this:
new ParallelDeadlineGroup(
  new DriveAndTurnCommand(swerveModule, ...),  // One subsystem
  new RunIntakeCommand(intakeSubsystem, ...),  // Different subsystem - OK!
  new TrackTargetCommand(visionSubsystem, ...) // Different subsystem - OK!
)
```

### Path Following
- Integrate PathPlanner for predefined trajectories
- Use Ramsete or Pure Pursuit controllers
- Add odometry for position feedback
- Implement adaptive path correction

### Competition Autonomous
```java
new SequentialCommandGroup(
  // Leave starting zone with curve
  new ParallelDeadlineGroup(
    new DriveDistanceCommand(...),
    new RotateToTargetCommand(...)
  ),
  // Score game piece
  new ScoreCommand(...),
  // Return curved path
  new ParallelDeadlineGroup(
    new DriveDistanceCommand(...),
    new RotateToAngleCommand(...)
  )
)
```

## Notes

### Key Learning: Subsystem Requirements
The most important lesson from this stage:
- **Problem**: Initially tried `ParallelDeadlineGroup` with separate drive and turn commands
- **Error**: "Multiple commands in a parallel group cannot require the same subsystems"
- **Solution**: Combine motor control into single command when operating on same subsystem
- **Pattern**: Use parallel groups for **different subsystems**, combined commands for **same subsystem**

### Command Execution Model
```java
public void execute() {
  m_swerveModule.drive(m_driveSpeed);
  m_swerveModule.setSteeringPosition(m_targetSteerPosition);
}
```
Both method calls happen in the same scheduler cycle (~20ms intervals), providing effectively simultaneous control.

### Why This Works
- `drive()` controls drive motor (open-loop speed)
- `setSteeringPosition()` controls steering motor (closed-loop position)
- Different motors can be controlled independently
- Same subsystem requirement prevents scheduler conflicts
- No actual hardware conflict since motors are separate

### Real Swerve Drive Considerations
In full swerve drive (4 modules):
- Each module subsystem combines drive + steer control
- Four separate module subsystems can run in parallel (different subsystems)
- Requires inverse kinematics to coordinate all 4 modules
- This single-module demo demonstrates the per-module control pattern

### Motion Predictability

**Sequential (Stage 06):**
- ✅ Predictable final position and heading
- ✅ Easy to verify each step
- ❌ Slower (stops between actions)
- ❌ Less natural motion

**Combined Control (Stage 07):**
- ✅ Faster, more efficient
- ✅ Smoother, more natural
- ✅ Full control over both distance and angle
- ✅ Predictable - both targets reached before command ends

### Debugging Combined Commands
- Add separate SmartDashboard outputs for drive and steer targets
- Log both motor states in `execute()` to verify simultaneous control
- Test drive-only and steer-only first before combining
- Use `PrintCommand` to log segment transitions
