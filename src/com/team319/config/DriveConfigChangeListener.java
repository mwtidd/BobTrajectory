package com.team319.config;

import com.team254.lib.trajectory.TrajectoryGenerator;

public interface DriveConfigChangeListener {
	public void onDriveConfigChange(TrajectoryGenerator.Config config);
}
