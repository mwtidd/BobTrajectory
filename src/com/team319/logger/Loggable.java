package com.team319.logger;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Loggable {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Map<String, Object> properties = new HashMap<String, Object>();

	public void putProperty(String key, Object value){
		properties.put(key, value);
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public Map<String, Object> getProperties(){
		return this.properties;
	}

	public String toJsonString(){
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonString = mapper.writeValueAsString(this);
			return jsonString;
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}
