# BobTrajectory

A library that allows for on the fly trajectory generation. 

The build is a single jar that get's deployed to the RoboRio and the Trajectory Server.

## Server Setup 

On the Trajectory Server run via the command line by calling `java -jar bob-trajectory.jar`

## RoboRio Setup

Import the jar into your eclipse project into a lib directory.
Right Click the Jar > Build Path > Add to Build Path

Also include the following in your build.properties file:
lib.dir=${src.dir}/../lib

userLibs=${lib.dir}/bob-library-0.0.1.jar;${lib.dir}/bob-trajectory.jar

## FIRST Robot Code
In robot init start the clients:

    try{
        WaypointClient.start("10.3.19.21");
        TrajectoryClient.start("10.3.19.21");
    }catch(Exception e){
  	  e.printStackTrace();
    }


Create a command group and add two sequential commands
-Build Spline
-Drive Spline


### Command Examples

#### Command Group

    public class DriveAutoSpline extends CommandGroup{
    	public DriveAutoSpline() {
    		//pass a set a of waypoints to the sever
    		//wait for response
    		//load trajectory into drivetrain
    		addSequential(new BuildSingleTowerSpline());
    		
    		//run the trajectory (stored in the drivetrain from the server) 
    		addSequential(new DriveSpline());
    	}
    }

#### Build Spline

    public class BuildDriveStraightSpline extends Command implements ITrajectoryChangeListener{
    	
    	private static final double BACK_OFF = 3.5;
    	private boolean waitingForTrajectory = true;
    	
    	public BuildDriveStraightSpline() {
    		requires(Robot.driveTrain);
    	}
    
    	@Override
    	protected void initialize() {
    		// TODO Auto-generated method stub
    		TrajectoryManager.getInstance().registerListener(this);
    		
    		List<Waypoint> waypoints = new ArrayList<Waypoint>();
    		waypoints.add(new Waypoint(0,0,0));		
    		waypoints.add(new Waypoint(10,0,0));
    		waypoints.add(new Waypoint(16,-2,0));
    		
    		WaypointList waypointList = new WaypointList(waypoints);
    		WaypointManager.getInstance().setWaypointList(waypointList, null);
    	    
    	}
    	@Override
    	protected void execute() {
    		// TODO Auto-generated method stub
    		
    	}
    
    	@Override
    	protected boolean isFinished() {
    		return !waitingForTrajectory;
    	}
    
    	@Override
    	protected void end() {
    		// TODO Auto-generated method stub
    		TrajectoryManager.getInstance().unregisterListener(this);
    		waitingForTrajectory = true;
    	}
    
    	@Override
    	protected void interrupted() {
    		// TODO Auto-generated method stub
    		
    	}
    	
    	
    	@Override
    	public void onTrajectoryChange(CombinedSrxMotionProfile combined, TrajectoryServletSocket source) {
    		System.out.println("Got Trajectory");
    		Robot.driveTrain.setCurrentProfile(combined);
    		waitingForTrajectory = false;
    	}
    
    }

#### Drive Spline

    public class DriveSpline extends Command{
	
        	private long startTime = 0;
        	private long maxTime = 0;
        	
        	int loops = 0;
        
        	//CANTalon rightDriveLead = RobotMap.driveTrainrightDriveLead;
        	//CANTalon leftDriveLead = RobotMap.driveTrainleftDriveLead;
        	
        	boolean motionProfileStarted = true;
        	//true indicates left profile
        	GeneratedMotionProfile leftProfile = new GeneratedMotionProfile(RobotMap.driveTrainleftDriveLead, true);
        	//false indicates right profile
        	GeneratedMotionProfile rightProfile = new GeneratedMotionProfile(RobotMap.driveTrainrightDriveLead, false);
        	
        	
        	public DriveSpline() {
        		requires(Robot.driveTrain);
        	}
        
        	@Override
        	protected void initialize() {
        		/**
        		//calculate the time it should take , used as an override to end the MP command based on a time
        		 
        		int leftCount = Robot.driveTrain.getCurrentProfile().getLeftProfile().getNumPoints();
        		int rightCount = Robot.driveTrain.getCurrentProfile().getRightProfile().getNumPoints();
        		
        		maxTime = Math.max(leftCount, rightCount) * 10; //in ms
        		startTime = System.currentTimeMillis();
        		**/
        		
        		Robot.driveTrain.shiftDown();
        	    loops = 0;
        	    
        	    rightProfile.reset();
        	    leftProfile.reset();
        	    
        	    motionProfileStarted = true;
        	}
        
        	@Override
        	protected void execute() {
        		rightProfile.control();
            	leftProfile.control();
        		
            	RobotMap.driveTrainrightDriveLead.changeControlMode(TalonControlMode.MotionProfile);
            	RobotMap.driveTrainleftDriveLead.changeControlMode(TalonControlMode.MotionProfile);
            	
            	CANTalon.SetValueMotionProfile setRightOutput = rightProfile.getSetValue();
                CANTalon.SetValueMotionProfile setLeftOutput = leftProfile.getSetValue();
            	
                RobotMap.driveTrainrightDriveLead.set(setRightOutput.value);
            	RobotMap.driveTrainleftDriveLead.set(setLeftOutput.value);
            	
            	if(motionProfileStarted){
                	rightProfile.startMotionProfile();
                	leftProfile.startMotionProfile();
                		
                	motionProfileStarted = false;
            	}
        	}
        
        	@Override
        	protected boolean isFinished() {
        		//for testing purposes
        		/**
        		if(true){
        			return true;
        		}
        		**/
        		/**
        		if(System.currentTimeMillis() - startTime > maxTime){
        			return true;
        		}else 
        		**/
        			
        		if( rightProfile.getTimeoutCnt() >2 || leftProfile.getTimeoutCnt() >2){
        	    	return true;
            	}else if (rightProfile.isFinished()==true && leftProfile.isFinished()==true){
        	        return true;
            	}else{
        	    	return false;
            	}
        	}
        
        	@Override
        	protected void end() {
        		RobotMap.driveTrainrightDriveLead.changeControlMode(TalonControlMode.PercentVbus);
            	RobotMap.driveTrainrightDriveFollow.changeControlMode(TalonControlMode.Follower);
            	RobotMap.driveTrainrightDriveFollow.set(RobotMap.driveTrainrightDriveLead.getDeviceID());
             	RobotMap.driveTrainleftDriveLead.changeControlMode(TalonControlMode.PercentVbus);
             	RobotMap.driveTrainleftDriveFollow.changeControlMode(TalonControlMode.Follower);
             	RobotMap.driveTrainleftDriveFollow.set(RobotMap.driveTrainleftDriveLead.getDeviceID());
            	
            	
            	RobotMap.driveTrainleftDriveLead.set(0);
            	RobotMap.driveTrainrightDriveLead.set(0);
            	
            	rightProfile.reset();
            	leftProfile.reset();
            	
            	//Our profile has been run, so let's empty it
            	Robot.driveTrain.setCurrentProfile(null);
        	}
        
        	@Override
        	protected void interrupted() {
        		// TODO Auto-generated method stub
        	}
        
        }
