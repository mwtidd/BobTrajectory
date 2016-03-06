package com.team319.pid.status;

import java.util.List;

import com.team254.lib.trajectory.TrajectoryGenerator;

public interface IPidStatusChangeListener {
	public void onPidStatusChange(PidStatus status);
}
