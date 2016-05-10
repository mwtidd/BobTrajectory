package com.team319.vision;

public class Target {
	private double horizontalOffset = 0;
	private double verticalOffset = 0;
	private double distance = 0;

	public Target(){

	}

	public Target(double horizontalOffset, double verticalOffset, double distance){
		this.verticalOffset = verticalOffset;
		this.horizontalOffset = horizontalOffset;
		this.distance = distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void setHorizontalOffset(double horizontalOffset) {
		this.horizontalOffset = horizontalOffset;
	}

	public void setVerticalOffset(double verticalOffset) {
		this.verticalOffset = verticalOffset;
	}

	public double getHorizontalOffset() {
		return horizontalOffset;
	}

	public double getVerticalOffset() {
		return verticalOffset;
	}

	public double getDistance() {
		return distance;
	}

}
