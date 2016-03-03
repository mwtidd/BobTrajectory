package com.team319.robot.commands;

import java.util.List;

import com.team319.waypoint.Waypoint;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Subsystem;

public class BuildAndDriveTrajectory extends CommandGroup{

	/**
	 *
	 * A Command Group that handles both the building and the execution of a trajectory
	 * based on a set of waypoints.
	 *
	 * @param driveTrain	The drive train subsystem that is be required by the commands
	 * @param leftTalon		The CAN Talon on the left side of the drive train
	 * @param rightTalon	The CAN Talon on the right side of the drive train
	 * @param waypoints 	The List of Waypoints that the drive train should drive through
	 */
	public BuildAndDriveTrajectory(Subsystem driveTrain, CANTalon leftTalon, CANTalon rightTalon, List<Waypoint> waypoints) {
		//pass a set a of waypoints to the sever
		//wait for response
		//load trajectory into drivetrain
		addSequential(new BuildTrajectory(driveTrain, waypoints));

		//run the trajectory (stored in the TrajectoryManager from the server)
		addSequential(new DriveTrajectory(driveTrain, leftTalon, rightTalon));
	}
}
