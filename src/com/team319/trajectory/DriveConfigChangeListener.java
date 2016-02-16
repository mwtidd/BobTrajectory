package com.team319.trajectory;

import java.util.List;

import com.team254.lib.trajectory.TrajectoryGenerator;

public interface DriveConfigChangeListener {
	public void onDriveConfigChange(TrajectoryGenerator.Config config);
}
