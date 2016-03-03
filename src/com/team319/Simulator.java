package com.team319;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.config.ConfigManager;
import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.trajectory.ITrajectoryChangeListener;
import com.team319.trajectory.progress.CombinedTrajectoryProgress;
import com.team319.trajectory.progress.TrajectoryProgress;
import com.team319.trajectory.progress.TrajectoryProgressManager;

public class Simulator implements ITrajectoryChangeListener {

	private static Logger logger = LoggerFactory.getLogger(Simulator.class);


	@Override
	public void onTrajectoryChange(CombinedSrxMotionProfile latestProfile) {
		long startTime = System.currentTimeMillis();

		long totalTime = (long) (1000 * ConfigManager.getInstance().getConfig().getDt() * latestProfile.getLeftProfile().getNumPoints());

		double elapsedTime = 0;

		double maxVelocity = 15;
		double maxAcc = 5;

		double velocity = 0;
		double distance = 0;

		int index = 0;

		while(index < latestProfile.getLeftProfile().getPoints().length && index <  latestProfile.getRightProfile().getPoints().length){

			double[] leftPoint = latestProfile.getLeftProfile().getPoints()[index];
			double[] rightPoint = latestProfile.getRightProfile().getPoints()[index];

			elapsedTime = System.currentTimeMillis() - startTime;

			if(velocity < maxVelocity){
				velocity += (maxAcc/1000);
			}

			TrajectoryProgress left = new TrajectoryProgress();

			left.setElapsedTime(index * 1000 * ConfigManager.getInstance().getConfig().getDt());
			left.setActualDistance(leftPoint[0]);
			left.setTargetDistance(leftPoint[0]);
			left.setTargetVelocity(leftPoint[1]);
			left.setActualVelocity(leftPoint[1]);
			TrajectoryProgress right = new TrajectoryProgress();

			right.setElapsedTime(index * 1000 * ConfigManager.getInstance().getConfig().getDt());
			right.setActualDistance(rightPoint[0]);
			right.setTargetDistance(rightPoint[0]);
			right.setTargetVelocity(rightPoint[1]);
			right.setActualVelocity(rightPoint[1]);

			TrajectoryProgressManager.getInstance().setProgress(new CombinedTrajectoryProgress(left,right));

			long sleepTime = (long) (1000 * ConfigManager.getInstance().getConfig().getDt());

			index++;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				logger.error("unable to sleep");
			}

		}
	}
}
