package org.usfirst.frc.team5987.robot.commands;


import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team5987.robot.subsystems.DriveSubsystem;
import org.usfirst.frc.team5987.robot.subsystems.Robot;
import org.usfirst.frc.team5987.robot.subsystems.RobotMap;

import static org.usfirst.frc.team5987.robot.subsystems.Robot.driveTable;

public class DriveByPID extends Command {

    private DriveSubsystem driveSubsystem = Robot.m_drivesubsystem;
    public double targetDistance;
    public final double deltaTime = 0.02;
    public double error;
    public double Integral = 0;
    public double lastError = error;
    public double derivative;
    public double distance;

    NetworkTableEntry pEntry = driveTable.getEntry("P");
    NetworkTableEntry iEntry = driveTable.getEntry("I");
    NetworkTableEntry dEntry = driveTable.getEntry("D");

    public DriveByPID(double distance){
        this.distance = distance;
    }

    @Override
    protected void initialize() {
        targetDistance = distance + driveSubsystem.getDistance();
        driveSubsystem.setSpeed(0);
    }

    @Override
    protected void execute() {
        update();
        error = targetDistance - driveSubsystem.getDistance(); //proportional
        Integral += error * deltaTime;
        derivative = (error - lastError) / deltaTime;
        driveSubsystem.setSpeed(RobotMap.Kp * error + RobotMap.Ki * Integral + RobotMap.Kd * derivative); //PID
        lastError = error;
    }

    @Override
    protected boolean isFinished() {
        return error >= -0.01 && error <= 0.01;
    }

    @Override
    protected void interrupted() {
        end();
    }

    @Override
    protected void end() {

    }

    private void update() {
        RobotMap.Kp = pEntry.getDouble(RobotMap.Kp);
        RobotMap.Ki = iEntry.getDouble(RobotMap.Ki);
        RobotMap.Kd = dEntry.getDouble(RobotMap.Kd);

    }
}

