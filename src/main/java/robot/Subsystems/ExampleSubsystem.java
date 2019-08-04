package robot.Subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ExampleSubsystem extends Subsystem {
    public static final TalonSRX masterRight = new TalonSRX(Constants.rightMaster);
    public static final TalonSRX masterLeft = new TalonSRX(Constants.leftMaster);
    public static final VictorSPX slaveLeft1 = new VictorSPX(Constants.slaveLeft1);
    public static final VictorSPX slaveLeft2 = new VictorSPX(Constants.slaveLeft2);
    public static final VictorSPX slaveRight1 = new VictorSPX(Constants.slaveRight1);
    public static final VictorSPX slaveRight2 = new VictorSPX(Constants.slaveRight2);


    @Override
    protected void initDefaultCommand() {

    }

    public static void setSlavesToFollow(){
        slaveLeft1.follow(masterLeft);
        slaveLeft2.follow(masterLeft);
        slaveRight1.follow(masterRight);
        slaveRight2.follow(masterRight);

    }
    public static void setSpeed(double speed) {
        if (speed > 1) speed = .2;
        else if (speed < -1) speed = -.2;
        masterLeft.set(ControlMode.PercentOutput, speed);
        masterRight.set(ControlMode.PercentOutput, speed);
    }



}
