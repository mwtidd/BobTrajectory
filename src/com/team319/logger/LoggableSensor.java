package com.team319.logger;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name","properties"})
public abstract class LoggableSensor extends Loggable{
	private String name;

	public LoggableSensor(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
