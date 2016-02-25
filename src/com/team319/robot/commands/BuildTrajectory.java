package com.team319.robot.commands;

import java.util.List;

import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.waypoint.WaypointList;
import com.team319.waypoint.WaypointManager;
import com.team319.trajectory.ITrajectoryChangeListener;
import com.team319.trajectory.TrajectoryManager;
import com.team319.waypoint.Waypoint;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

public class BuildTrajectory extends Command implements ITrajectoryChangeListener{

	private boolean waitingForTrajectory = true;

	List<Waypoint> waypoints;

	public BuildTrajectory(Subsystem drivetrain, List<Waypoint> waypoints) {
		requires(drivetrain);
		this.waypoints = waypoints;
	}

	@Override
	protected void initialize() {
		waitingForTrajectory = true;
		//when we get the trajectory back run `onTrajectoryChange`
		TrajectoryManager.getInstance().registerListener(this);

		try {
			//send the waypoints to the server
			WaypointManager.getInstance().setWaypointList(new WaypointList(waypoints));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void execute() {
		//continue waiting
	}

	@Override
	protected boolean isFinished() {
		return !waitingForTrajectory;
	}

	@Override
	protected void end() {
		//we don't need to be told of trajectory changes going forward
		TrajectoryManager.getInstance().unregisterListener(this);
	}

	@Override
	protected void interrupted() {
		//we don't need to be told of trajectory changes going forward
		TrajectoryManager.getInstance().unregisterListener(this);

	}

	@Override
	public void onTrajectoryChange(CombinedSrxMotionProfile combine) {
		//we're done! on to bigger and better things
		waitingForTrajectory = false;
	}

}
