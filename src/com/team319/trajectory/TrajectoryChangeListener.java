package com.team319.trajectory;

import java.util.List;

public interface TrajectoryChangeListener {
	public void onTrajectoryChange(CombinedSrxMotionProfile latestProfile);
}
