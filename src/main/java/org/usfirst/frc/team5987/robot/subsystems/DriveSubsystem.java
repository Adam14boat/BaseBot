package org.usfirst.frc.team5987.robot.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveSubsystem extends Subsystem {
    private static final VictorSP leftMotor1 = new VictorSP(RobotMap.driveLeftMotorChannel);
    private static final VictorSP leftMotor2 = new VictorSP(RobotMap.driveRightMotorChannel);
    private final Encoder encoder = new Encoder(RobotMap.driveEncoderChannelA, RobotMap.driveEncoderChannelB);

    public void initDefaultCommand() {

    }

    public DriveSubsystem() {
        leftMotor1.setInverted(RobotMap.driveMotorInverted);
        encoder.setDistancePerPulse(RobotMap.driveDistancePerPulse);
    }


    public void setSpeed(double speed) {
        leftMotor1.setSpeed(speed);
        leftMotor2.setSpeed(speed);
    }

    public double getSpeed() {
        return encoder.getRate();
    }

    public double getDistance() {
        return encoder.getDistance();
    }

}

