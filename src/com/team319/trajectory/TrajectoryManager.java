package com.team319.trajectory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team254.lib.trajectory.Path;
import com.team319.HistoryBundle;
import com.team319.config.ConfigManager;
import com.team319.config.DriveConfig;
import com.team319.path.PathHistory;
import com.team319.path.PathManager;
import com.team319.waypoint.WaypointList;
import com.team319.waypoint.WaypointManager;

/**
 * This is used both on the robot and the trajectory sever.
 * On the robot is is used to update a listener (in our case the TowerCamera)
 * On the server is is used to update any sockets listening for a new trajectory
 *
 * @author mwtidd
 *
 */
public class TrajectoryManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static TrajectoryManager instance = null;

	private List<ITrajectoryChangeListener> listeners;

	private CombinedSrxMotionProfile latestProfile;

	private static final String PATH_NAME = "Path";

	private TrajectoryManager(){
		listeners = new ArrayList<ITrajectoryChangeListener>();
		latestProfile = null;
	}

	public static TrajectoryManager getInstance(){
		if(instance == null){
			instance = new TrajectoryManager();
		}
		return instance;
	}

	public void registerListener(ITrajectoryChangeListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(ITrajectoryChangeListener listener){
		this.listeners.remove(listener);
	}

	public void setLatestProfile(CombinedSrxMotionProfile latestProfile){
		//the profile has changed, we should let everyone know
		this.latestProfile = latestProfile;
		for(ITrajectoryChangeListener listener : listeners){
			listener.onTrajectoryChange(latestProfile);
		}
	}

	public CombinedSrxMotionProfile getLatestProfile() {
		return latestProfile;
	}

	public void generateTrajectory(){

		long startTime = System.currentTimeMillis();

		WaypointList waypointList = WaypointManager.getInstance().getWaypointList();
		DriveConfig config = ConfigManager.getInstance().getConfig();

		HistoryBundle bundle = new HistoryBundle(config, waypointList);

		try {
			if(!waypointList.isCachable() || !TrajectoryHistory.getInstance().hasBundle(new HistoryBundle(config, waypointList))){



				//looks good, let's generate a chezy path and trajectory

				Path path = PathManager.getInstance().makeChezyPath(bundle);

				logger.info("Path Gen Finished @ " + (System.currentTimeMillis() - startTime) + "ms");

				SRXTranslator srxt = new SRXTranslator();
				CombinedSrxMotionProfile combined = srxt.getSrxProfileFromChezyPath(path, 5.875, 1.57);//2.778);

				logger.info("SRXing Finished @ " + (System.currentTimeMillis() - startTime) + "ms");

				//the trajectory looks good, lets pass it back
				setLatestProfile(combined);



				if(waypointList.isCachable()){
					String id = writeTrajectory(combined);

					TrajectoryHistory.getInstance().putTrajectory(bundle, id);
				}
			}else{

				PathManager.getInstance().getStoredPath(bundle);

				String id = TrajectoryHistory.getInstance().getId(bundle);
				CombinedSrxMotionProfile combined = readTrajectory(id);
				setLatestProfile(combined);
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		logger.info("Total Gen Finished @ " + (System.currentTimeMillis() - startTime) + "ms");

		//sendTrajectory();
	}

	private String writeTrajectory(CombinedSrxMotionProfile data) throws IOException {
		UUID id = UUID.randomUUID();


		File file = new File("trajectories/"+id.toString()+".json");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(data.toJsonString());
		bw.close();

		return id.toString();
	}



	private CombinedSrxMotionProfile readTrajectory(String id) throws FileNotFoundException, IOException, JsonParseException, JsonMappingException{
		try(BufferedReader br = new BufferedReader(new FileReader("trajectories/" + id + ".json"))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();

    		ObjectMapper mapper = new ObjectMapper();

    		CombinedSrxMotionProfile profile = mapper.readValue(everything, CombinedSrxMotionProfile.class);

    		return profile;

		}
	}

	public void loadTrajectories() throws JsonParseException, JsonMappingException, FileNotFoundException, IOException{
		logger.info("Loading Trajectories");

		//load trajectory map
		TrajectoryHistory.getInstance().loadHistory();
		PathHistory.getInstance().loadHistory();
		//load trajectories
	}

}
