# Stage 06 - Complex Autonomous with Command Groups
Date: 2025-11-16

## Summary
Created a complex autonomous routine using command groups to sequence multiple movements. The `GoToGoalCommand` chains five separate actions (three drives and two turns) into a single coordinated autonomous routine.

## New Capability / Rationale
- **Functional Addition**: Multi-step autonomous routine combining driving and turning
- **Motivation**: Demonstrates command composition - building complex behaviors from simple reusable commands. This is a fundamental FRC programming pattern where `SequentialCommandGroup` orchestrates multiple commands to execute one after another. Essential for real competition autonomous routines that require coordinated sequences of actions.

## Implementation Highlights

### New Command Class
- **`GoToGoalCommand`** (commands/GoToGoalCommand.java)
  - Extends `SequentialCommandGroup` (not basic `Command`)
  - Constructs a five-step autonomous sequence in the constructor
  - No need for `initialize()`, `execute()`, `isFinished()`, or `end()` - the group handles lifecycle
  - Reuses existing commands: `DriveDistanceCommand` and `RotateToAngleCommand`

### Autonomous Sequence
```
Step 1: Drive forward 20 rotations at 10% speed
   ↓
Step 2: Turn clockwise 90° (0.25 rotations)
   ↓
Step 3: Drive forward 20 rotations at 10% speed
   ↓
Step 4: Turn counterclockwise 90° (-0.25 rotations)
   ↓
Step 5: Drive forward 20 rotations at 10% speed
```

### Key Classes Modified
- **`RobotContainer`** (RobotContainer.java)
  - Updated `getAutonomousCommand()` to return `GoToGoalCommand`
  - **Before**: Simple single command - `DriveDistanceCommand(30, 0.2)`
  - **After**: Complex command group - `GoToGoalCommand(m_swerveModule)`
  - Removed unused `DriveDistanceCommand` import (now only used within `GoToGoalCommand`)

### Configuration Parameters
- **Drive distance**: 20 rotations per segment (60 total)
- **Drive speed**: 0.1 (10% - slower for safety and precision)
- **Turn angles**: ±0.25 rotations (±90 degrees)
- **Total steps**: 5 commands executed sequentially

## Command Group Architecture

### SequentialCommandGroup Benefits
- **Composition**: Build complex routines from simple building blocks
- **Reusability**: Same commands used in teleop (X/B buttons) and autonomous
- **Maintainability**: Changes to `DriveDistanceCommand` automatically apply to all uses
- **Readability**: Autonomous routine is self-documenting in constructor
- **State management**: Group automatically handles command lifecycle

### Command Execution Flow
```
GoToGoalCommand scheduled
  ↓
Step 1 initialize() → execute() loop → isFinished() = true → end()
  ↓
Step 2 initialize() → execute() loop → isFinished() = true → end()
  ↓
Step 3 initialize() → execute() loop → isFinished() = true → end()
  ↓
Step 4 initialize() → execute() loop → isFinished() = true → end()
  ↓
Step 5 initialize() → execute() loop → isFinished() = true → end()
  ↓
GoToGoalCommand complete
```

### Alternative Group Types
- **`SequentialCommandGroup`**: Commands run one after another (used here)
- **`ParallelCommandGroup`**: Commands run simultaneously until all finish
- **`ParallelRaceGroup`**: Commands run simultaneously until first finishes
- **`ParallelDeadlineGroup`**: Commands run simultaneously until deadline command finishes

## Testing / Verification

### Build
```bash
./gradlew build
```
Build successful - no compilation errors.

### Deployment Steps
1. Deploy to roboRIO: `./gradlew deploy`
2. Enable autonomous mode
3. **Ensure large open space** - robot will move in a complex path
4. Observe full sequence execution
5. Robot should end in different position/orientation than start

### Expected Behaviors
- **Autonomous start**: Immediately begins driving forward at 10% speed
- **Step transitions**: Brief pause as each command completes and next begins
- **Turn behavior**: Steering rotates, drive stops during turns
- **Completion**: Robot stops after final 20-rotation drive
- **Total time**: Varies based on motor speeds and distances

### Path Visualization
```
Start →→→→→→→→→→→ (20 rotations)
                  ↓ (turn 90° CW)
                  →→→→→→→→→→→ (20 rotations)
                             ↑ (turn 90° CCW)
          ←←←←←←←←←←← (20 rotations forward)
```
Robot ends displaced from start in both X and Y directions.

### SmartDashboard Monitoring
- **"Swerve/Angle"**: Changes during turn steps, constant during drive steps
- Consider adding current command name to dashboard for debugging
- Monitor drive position to verify each segment completes

## Command Composition Patterns

### Building Blocks Reused
| Command | Original Use | Now Also Used In |
|---------|--------------|------------------|
| `DriveDistanceCommand` | Autonomous Stage 05 | GoToGoalCommand (3x) |
| `RotateToAngleCommand` | B button (Stage 04) | GoToGoalCommand (2x) |

### Code Comparison: Explicit vs Grouped

**Without Command Groups (would require custom command):**
```java
// Complex state machine tracking which step we're on
// Lots of if/else logic in execute()
// Error-prone step transitions
```

**With Command Groups (actual implementation):**
```java
addCommands(
  new DriveDistanceCommand(swerveModule, 20, 0.1),
  new RotateToAngleCommand(swerveModule, 0.25),
  // ... clear, declarative sequence
);
```

## Next Steps / Extensions

### Enhanced Autonomous Routines
- Create multiple autonomous routines (different paths)
- Implement autonomous selector (choose routine via dashboard)
- Add parallel operations (drive while turning for arc motion)
- Use `WaitCommand` for timed pauses between actions

### Advanced Sequencing
```java
new SequentialCommandGroup(
  new ParallelCommandGroup(
    new DriveDistanceCommand(...),
    new IntakeCommand(...)  // Future: run intake while driving
  ),
  new RotateToAngleCommand(...),
  new WaitCommand(0.5),  // Pause half second
  new ShootCommand(...)  // Future: scoring mechanism
);
```

### Path Planning
- Integrate PathPlanner or trajectory following
- Add waypoint-based navigation
- Implement odometry for position tracking
- Use vision for dynamic target adjustment

### Safety & Robustness
- Add timeout to entire routine (max 15 seconds)
- Implement obstacle detection/avoidance
- Add acceleration limiting between steps
- Create abort command (emergency stop)

## Notes

### Command Group Ownership
- `SequentialCommandGroup` takes ownership of child commands
- Child commands are scheduled/cancelled automatically
- No need to manually track state - framework handles it

### Interruption Behavior
- If autonomous disabled mid-execution, group cancels all commands
- Current command's `end(true)` is called (interrupted = true)
- Remaining commands never execute
- Motors stop safely via command cleanup

### Subsystem Requirements
- All child commands require same subsystem (`m_swerveModule`)
- Sequential execution ensures no command conflicts
- Subsystem is locked for entire group duration

### Debugging Tips
- Test individual commands first before grouping
- Use SmartDashboard to show current step
- Consider adding `PrintCommand` between steps for logging
- Break complex groups into smaller sub-groups for testing

### Real-World Considerations
- Current implementation assumes no obstacles
- No feedback correction (open-loop navigation)
- Battery voltage affects actual distances traveled
- For competition: add odometry, vision, and error correction
