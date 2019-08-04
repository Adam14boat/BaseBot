package robot.Subsystems.Commands;

import edu.wpi.first.wpilibj.command.Command;
import robot.Robot;
import robot.Subsystems.ExampleSubsystem;

public class ExampleCommand extends Command {

    @Override
    protected void initialize() {
        Robot.example.setSpeed(0.3);
    }



    @Override
    protected void execute() {

    }

    @Override
    protected boolean isFinished() {

        return false;
    }

    @Override
    protected void interrupted() {
    }

    @Override
    protected void end() {

    }
}
