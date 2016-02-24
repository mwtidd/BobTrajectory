package com.team319.trajectory;

import java.util.List;

import com.team319.web.trajectory.server.TrajectoryServletSocket;

public interface ITrajectoryChangeListener {
	public void onTrajectoryChange(CombinedSrxMotionProfile latestProfile);
}
