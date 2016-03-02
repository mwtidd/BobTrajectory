package com.team319.path;

import com.team254.lib.trajectory.Trajectory.Segment;

public class Point {
	private double x;
	private double y;

	public Point(){

	}

	public Point(Segment segment){
		this.x = segment.x;
		this.y = segment.y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}
