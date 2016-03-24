package com.team319.logger;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.robot.subsytems.StatefulSubsystem;

public class RobotLogger {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static RobotLogger instance = null;

	private List<StatefulSubsystem> subsystems = new ArrayList<StatefulSubsystem>();

	private Thread thread;

	private List<LogSender> senders;


	private RobotLogger(){
		senders = new ArrayList<LogSender>();
	}

	public static RobotLogger getInstance(){
		if(instance == null){
			instance = new RobotLogger();
		}
		return instance;
	}

	public void registerSender(LogSender sender){
		this.senders.add(sender);
	}

	public void unregisterSender(LogSender sender){
		this.senders.remove(sender);
	}

	public void registerSubsystem(StatefulSubsystem subsystem){
		this.subsystems.add(subsystem);
	}

	public void start(){
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while(!Thread.interrupted()){
					try {
						logger.debug("Send Robot");
						for(LogSender sender : RobotLogger.getInstance().getSenders()){
							sender.sendRobotJson(RobotLogger.getInstance().getJsonString());
						}
						Thread.sleep(100);
					} catch (InterruptedException e) {
						logger.error("Unable to sleep logging thread.");
					}
				}

			}
		});
		thread.start();
	}

	public void stop(){
		thread.interrupt();
	}

	public String getJsonString(){
		LoggableRobot robot = new LoggableRobot();
		for(StatefulSubsystem subsystem: subsystems){
			robot.addSubsystem(new LoggableSubsystem(subsystem));
		}

		ObjectMapper mapper = new ObjectMapper();
		try {
			String robotJson = mapper.writeValueAsString(robot);
			return robotJson;
		} catch (JsonProcessingException e) {
			logger.error("Unable to parse robot json.");
			return null;
		}
	}

	public List<LogSender> getSenders() {
		return senders;
	}

}
