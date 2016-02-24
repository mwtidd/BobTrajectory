package com.team319.trajectory;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

public class TrajectoryHistory {
	private Map<String, String> trajectoryMap = new HashMap<String, String>();

	private static TrajectoryHistory instance;

	public static TrajectoryHistory getInstance(){
		if(instance == null){
			instance = new TrajectoryHistory();
		}
		return instance;
	}

	public boolean hasBundle(TrajectoryBundle bundle) throws JsonProcessingException{
		return trajectoryMap.containsKey(bundle.toJsonString());
	}

	public String getId(TrajectoryBundle bundle) throws JsonProcessingException{
		return trajectoryMap.get(bundle.toJsonString());
	}

	public void putTrajectory(TrajectoryBundle trajectoryBundle, String id) throws JsonProcessingException {
		trajectoryMap.put(trajectoryBundle.toJsonString(), id);
	}

}
