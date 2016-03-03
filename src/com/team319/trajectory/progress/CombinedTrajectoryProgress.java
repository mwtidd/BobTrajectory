package com.team319.trajectory.progress;

public class CombinedTrajectoryProgress {
	private TrajectoryProgress leftProgress;
	private TrajectoryProgress rightProgress;

	public CombinedTrajectoryProgress(){

	}

	public CombinedTrajectoryProgress(TrajectoryProgress leftProgress, TrajectoryProgress rightProgress){
		this.leftProgress = leftProgress;
		this.rightProgress = rightProgress;
	}

	public TrajectoryProgress getLeftProgress() {
		return leftProgress;
	}

	public TrajectoryProgress getRightProgress() {
		return rightProgress;
	}
}
