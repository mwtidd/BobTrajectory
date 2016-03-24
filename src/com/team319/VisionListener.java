package com.team319;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team319.vision.ITargetListener;
import com.team319.vision.Target;

public class VisionListener implements ITargetListener{

	private Logger logger = LoggerFactory.getLogger(this.getClass());


	@Override
	public void onTargetChange(Target target) {
			logger.info("Got New Target");
	}
}
