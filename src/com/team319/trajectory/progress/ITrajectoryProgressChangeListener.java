package com.team319.trajectory.progress;

import java.util.List;

import com.team319.web.trajectory.server.TrajectoryServletSocket;

public interface ITrajectoryProgressChangeListener {
	public void onProgressChange(CombinedTrajectoryProgress progress);
}
