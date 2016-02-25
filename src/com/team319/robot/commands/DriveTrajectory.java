package com.team319.robot.commands;

import com.team319.robot.motion.OnTheFlyMotionProfile;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

public class DriveTrajectory extends Command{

	private long startTime = 0;
	private long maxTime = 0;

	int loops = 0;

	boolean motionProfileStarted = true;
	//true indicates left profile
	OnTheFlyMotionProfile leftProfile;
	//false indicates right profile
	OnTheFlyMotionProfile rightProfile;

	CANTalon leftTalon;
	CANTalon rightTalon;


	public DriveTrajectory(Subsystem driveTrain, CANTalon leftTalon, CANTalon rightTalon) {
		requires(driveTrain);
		//true indicates left profile
		leftProfile = new OnTheFlyMotionProfile(leftTalon, true);
		//false indicates right profile
		rightProfile = new OnTheFlyMotionProfile(rightTalon, false);

		this.leftTalon = leftTalon;
		this.rightTalon = rightTalon;
	}

	@Override
	protected void initialize() {
		/**
		//calculate the time it should take , used as an override to end the MP command based on a time

		int leftCount = Robot.driveTrain.getCurrentProfile().getLeftProfile().getNumPoints();
		int rightCount = Robot.driveTrain.getCurrentProfile().getRightProfile().getNumPoints();

		maxTime = Math.max(leftCount, rightCount) * 10; //in ms
		startTime = System.currentTimeMillis();
		**/

		//TODO: This should be done somewhere else
		//Robot.driveTrain.shiftDown();
	    loops = 0;

	    rightProfile.reset();
	    leftProfile.reset();

	    motionProfileStarted = true;
	}

	@Override
	protected void execute() {
		rightProfile.control();
    	leftProfile.control();

    	rightTalon.changeControlMode(TalonControlMode.MotionProfile);
    	leftTalon.changeControlMode(TalonControlMode.MotionProfile);

    	CANTalon.SetValueMotionProfile setRightOutput = rightProfile.getSetValue();
        CANTalon.SetValueMotionProfile setLeftOutput = leftProfile.getSetValue();

        rightTalon.set(setRightOutput.value);
    	leftTalon.set(setLeftOutput.value);

    	if(motionProfileStarted){
        	rightProfile.startMotionProfile();
        	leftProfile.startMotionProfile();

        	motionProfileStarted = false;
    	}
	}

	@Override
	protected boolean isFinished() {
		/**
		if(System.currentTimeMillis() - startTime > maxTime){
			return true;
		}else
		**/
		if( rightProfile.getTimeoutCnt() >2 || leftProfile.getTimeoutCnt() >2){
	    	return true;
    	}else if (rightProfile.isFinished()==true && leftProfile.isFinished()==true){
	        return true;
    	}else{
	    	return false;
    	}
	}

	@Override
	protected void end() {
		//TODO: all this should be done by the arcade drive command initialize
		/**
		RobotMap.driveTrainrightDriveLead.changeControlMode(TalonControlMode.PercentVbus);
    	RobotMap.driveTrainrightDriveFollow.changeControlMode(TalonControlMode.Follower);
    	RobotMap.driveTrainrightDriveFollow.set(RobotMap.driveTrainrightDriveLead.getDeviceID());
     	RobotMap.driveTrainleftDriveLead.changeControlMode(TalonControlMode.PercentVbus);
     	RobotMap.driveTrainleftDriveFollow.changeControlMode(TalonControlMode.Follower);
     	RobotMap.driveTrainleftDriveFollow.set(RobotMap.driveTrainleftDriveLead.getDeviceID());
    	RobotMap.driveTrainleftDriveLead.set(0);
    	RobotMap.driveTrainrightDriveLead.set(0);
    	**/

    	rightProfile.reset();
    	leftProfile.reset();

	}

	@Override
	protected void interrupted() {
		// TODO Auto-generated method stub
	}

}
