package com.team319.path;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Path {

	List<Point> leftPoints;

	List<Point> rightPoints;

	public Path(){

	}

	public Path(com.team254.lib.trajectory.Path path){
		leftPoints = new ArrayList<Point>();
		rightPoints = new ArrayList<Point>();

		for (int i = 0; i < path.getLeftWheelTrajectory().getNumSegments(); i++) {
			leftPoints.add(new Point(path.getLeftWheelTrajectory().getSegment(i)));
		}

		for (int i = 0; i < path.getRightWheelTrajectory().getNumSegments(); i++) {
			rightPoints.add(new Point(path.getRightWheelTrajectory().getSegment(i)));
		}
	}

	public String toJsonString() throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		String combinedJson = mapper.writeValueAsString(this);

		return combinedJson;
	}

	public List<Point> getLeftPoints() {
		return leftPoints;
	}

	public List<Point> getRightPoints() {
		return rightPoints;
	}
}
