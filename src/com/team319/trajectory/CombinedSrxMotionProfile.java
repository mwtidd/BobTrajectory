package com.team319.trajectory;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//Combines left and right motion profiles in one object
public class CombinedSrxMotionProfile {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private SrxMotionProfile leftProfile;
	private SrxMotionProfile rightProfile;

	public CombinedSrxMotionProfile(){
		this.leftProfile = null;
		this.rightProfile = null;
	}

	public CombinedSrxMotionProfile(SrxMotionProfile left, SrxMotionProfile right) {
		this.leftProfile = left;
		this.rightProfile = right;
	}

	public CombinedSrxMotionProfile(JSONObject combinedProfile){
		leftProfile = new SrxMotionProfile((JSONObject) combinedProfile.get("left"));
		rightProfile = new SrxMotionProfile((JSONObject) combinedProfile.get("right"));
	}

	public JSONObject toJson(){
		JSONObject combinedProfile = new JSONObject();
		combinedProfile.put("left", leftProfile.toJson());
		combinedProfile.put("right",rightProfile.toJson());
		return combinedProfile;
	}

	public SrxMotionProfile getLeftProfile() {
		return leftProfile;
	}

	public SrxMotionProfile getRightProfile() {
		return rightProfile;
	}

	public String toJsonString() throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		String combinedJson = mapper.writeValueAsString(this);

		return combinedJson;
	}

}
