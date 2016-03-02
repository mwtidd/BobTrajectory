package com.team319.config;

import java.util.List;

import com.team319.web.config.server.ConfigServletSocket;
import com.team319.web.trajectory.server.TrajectoryServletSocket;

public interface IConfigChangeListener {
	public void onConfigChange(DriveConfig config);
}
