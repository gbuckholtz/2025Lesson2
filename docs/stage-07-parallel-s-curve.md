# Stage 07 - Parallel Commands for S-Curve Motion
Date: 2025-11-16

## Summary
Refactored `GoToGoalCommand` to execute drive and steering commands simultaneously using `ParallelDeadlineGroup`, creating smooth S-curve motion instead of discrete drive-then-turn segments.

## New Capability / Rationale
- **Functional Addition**: Simultaneous drive and steering for arc/curve motion
- **Motivation**: Demonstrates parallel command execution patterns and shows how to create more natural, flowing robot motion. In real swerve drive systems, wheels can drive and steer simultaneously to create curved paths. This pattern is essential for smooth autonomous navigation, path following, and advanced maneuvers like arcade-style driving curves.

## Implementation Highlights

### Key Classes Modified
- **`GoToGoalCommand`** (commands/GoToGoalCommand.java)
  - Added import for `ParallelDeadlineGroup`
  - Reduced from 5 sequential steps to 3 parallel segments
  - Each segment runs drive and turn commands simultaneously
  - Drive command acts as "deadline" - determines when segment ends

### Command Structure Comparison

**Stage 06 (Sequential):**
```
5 steps total:
Drive → Turn → Drive → Turn → Drive
(stop between each action)
```

**Stage 07 (Parallel):**
```
3 segments total:
[Drive + Turn] → [Drive + Turn] → [Drive + Turn]
(smooth continuous motion)
```

### Parallel Deadline Group Pattern
```java
new ParallelDeadlineGroup(
  new DriveDistanceCommand(swerveModule, 20, 0.1),  // Deadline command
  new RotateToAngleCommand(swerveModule, 0.25)       // Interrupted when deadline finishes
)
```

- **Deadline command**: `DriveDistanceCommand` - runs until 20 rotations
- **Secondary command**: `RotateToAngleCommand` - may not complete full 90°
- **Behavior**: When drive finishes, turn is interrupted immediately
- **Result**: Creates arc motion with varying curvature

## Motion Pattern Analysis

### Stage 06 Path (Sequential)
```
Start →→→→→→→→→→→ (straight)
                  └─┐ (stop, rotate in place)
                    →→→→→→→→→→→ (straight)
                               ┌─┘ (stop, rotate in place)
                    ←←←←←←←←←←← (straight)
```
- Sharp corners, stop between actions
- Predictable but mechanical motion

### Stage 07 Path (Parallel S-Curve)
```
Start ~~~~~~~~> (smooth curve right)
       ~~~~~~~~> (smooth curve left)
                ~~~~~~~~> (smooth curve right)
```
- Flowing S-pattern, no stops
- More natural, efficient motion
- Actual path depends on relative speeds

### Curvature Characteristics
- **First segment**: Curves clockwise while moving forward
- **Second segment**: Curves counterclockwise (reverses curvature) → creates S-shape
- **Third segment**: Curves clockwise again
- **Arc radius**: Determined by ratio of drive speed to turn rate

## Parallel Command Group Types

### ParallelDeadlineGroup (Used Here)
- **Deadline**: One command determines when group ends
- **Others**: Interrupted when deadline finishes
- **Use case**: "Drive while doing X, stop everything when drive completes"

### Alternative Group Types

**ParallelCommandGroup:**
```java
new ParallelCommandGroup(
  new DriveDistanceCommand(...),
  new RotateToAngleCommand(...)
)
```
- Runs until ALL commands finish
- Both must complete their full targets
- Use case: "Wait for both drive AND turn to complete"

**ParallelRaceGroup:**
```java
new ParallelRaceGroup(
  new DriveDistanceCommand(...),
  new RotateToAngleCommand(...)
)
```
- Runs until ANY command finishes
- First to complete ends the group
- Use case: "Drive OR turn, whichever happens first"

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
5. Compare to Stage 06 behavior (if recorded)

### Expected Behaviors
- **Autonomous start**: Immediately begins driving AND turning
- **Motion quality**: Smooth, continuous - no stops between segments
- **Path shape**: S-curve pattern (right-left-right curvature)
- **Completion**: Stops after third segment finishes
- **No pauses**: Transitions between segments are seamless

### Observation Points
- **SmartDashboard "Swerve/Angle"**: Continuously changing during entire routine
- **Drive position**: Steadily increasing (no stops)
- **Actual path**: May differ from ideal due to turn not completing full 90°

### Potential Variations
- If turn completes before drive (unlikely with current parameters), turn will hold final position
- If drive completes before turn reaches target, turn is interrupted mid-rotation
- Final heading depends on actual rotation achieved during drive

## Architectural Patterns

### Subsystem Sharing in Parallel Groups
Both commands require `m_swerveModule`, but they control different motors:
- `DriveDistanceCommand`: Controls drive motor
- `RotateToAngleCommand`: Controls steering motor
- **Legal in parallel**: Different motors, same subsystem
- Commands must be designed to not conflict

### Deadline Command Selection
Choose deadline based on desired behavior:
- **Drive as deadline** (current): Ensures specific distance traveled
- **Turn as deadline**: Would ensure specific angle reached
- **Time as deadline**: `WaitCommand` for duration-based segments

## Next Steps / Extensions

### Motion Refinement
- Tune drive/turn speeds for desired curve radius
- Add velocity ramping for smoother acceleration
- Implement closed-loop curve following (feedback control)
- Calculate arc radius: `radius = drive_velocity / angular_velocity`

### Advanced Parallel Patterns
```java
// Drive while running intake and vision tracking
new ParallelDeadlineGroup(
  new DriveDistanceCommand(...),    // Deadline
  new RotateToAngleCommand(...),    // Curve motion
  new RunIntakeCommand(...),        // Continuous intake
  new TrackTargetCommand(...)       // Vision alignment
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

### Interrupted Command Behavior
When deadline finishes:
- `RotateToAngleCommand.end(true)` is called with `interrupted = true`
- Steering motor state depends on `end()` implementation
- Current `RotateToAngleCommand` doesn't actively stop in `end()` - position holds via PID

### Subsystem Command Requirements
WPILib enforces:
- Commands must declare subsystem requirements
- Parallel commands can share subsystem if they don't conflict
- Drive and steer motors are independent - safe to run simultaneously
- Framework doesn't know about motor-level conflicts - programmer responsibility

### Real Swerve Drive Considerations
In full swerve drive (4 modules):
- Each module can drive and steer independently
- Parallel operation is fundamental to swerve capability
- Requires inverse kinematics to coordinate all 4 modules
- This single-module demo shows the principle at smaller scale

### Motion Predictability Trade-offs
**Sequential (Stage 06):**
- ✅ Predictable final position and heading
- ✅ Easy to verify each step
- ❌ Slower (stops between actions)
- ❌ Less natural motion

**Parallel (Stage 07):**
- ✅ Faster, more efficient
- ✅ Smoother, more natural
- ⚠️ Final heading depends on execution timing
- ⚠️ Harder to predict exact path

### Debugging Parallel Commands
- Add logging to `initialize()` and `end()` of each command
- Use `SmartDashboard.putBoolean("CommandName/isRunning", true)` in `execute()`
- Consider `PrintCommand` between groups for sequencing visibility
- Test individual commands before combining

### Performance Considerations
- Parallel execution doesn't increase CPU load significantly
- Commands still run sequentially in scheduler, just multiple per cycle
- Actual parallelism is at hardware level (different motors)
- Command overhead is minimal compared to motor control
