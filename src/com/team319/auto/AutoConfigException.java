package com.team319.auto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "__class")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutoConfigException {
	@JsonIgnore
	public static final String UNDEFINED_AUTO = "Undefined Auto";

	private String type;

	public String __class = AutoConfigException.class.getName();


	public AutoConfigException(){

	}

	public AutoConfigException(String type){
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
