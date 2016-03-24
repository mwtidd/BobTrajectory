package com.team319.waypoint;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class Waypoint {

	private double x = 0d;
	private double y = 0d;
	private double theta = 0d;

	public Waypoint(){

	}

	public Waypoint(double x, double y, double theta) {
      this.x = x;
      this.y = y;
      this.theta = theta;
    }

	public JSONObject toJson(){
		JSONObject waypoint = new JSONObject();
		waypoint.put("x", x);
		waypoint.put("y", y);
		waypoint.put("z", theta);
		return waypoint;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getTheta() {
		return theta;
	}

}
