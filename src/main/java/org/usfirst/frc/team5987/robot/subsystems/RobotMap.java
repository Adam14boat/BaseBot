/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5987.robot.subsystems;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap 
{
    public static final int driveLeftMotorChannel = 1;
    public static final int driveRightMotorChannel = 2;
    public static final boolean driveMotorInverted = false;
    public static int driveEncoderChannelB = 4;
    public static int driveEncoderChannelA = 3;
    public static double driveDistancePerPulse = 0.00133; //TODO: calculate the value of distance per pulse
    public static double Kp = 0.5;
    public static double Ki = 0.25;
    public static double Kd = 0.75;
    // For example to map the left and right motors, you could define the
    // following variables to use with your drivetrain subsystem.
    // public static int leftMotor1 = 1;
    // public static int leftMotor2 = 2;

    // If you are using multiple modules, make sure to define both the port
    // number and the module. For example you with a rangefinder:
    // public static int rangefinderPort = 1;
    // public static int rangefinderModule = 1;
}
