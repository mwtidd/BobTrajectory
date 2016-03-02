package com.team319.path;

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
import com.team254.lib.trajectory.PathGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team319.HistoryBundle;
import com.team319.config.ConfigManager;
import com.team319.config.DriveConfig;
import com.team319.trajectory.CombinedSrxMotionProfile;


/**
 * This is used both on the robot and the trajectory sever.
 * On the robot is is used to update a listener (in our case the TowerCamera)
 * On the server is is used to update any sockets listening for a new trajectory
 *
 * @author mwtidd
 *
 */
public class PathManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static PathManager instance = null;

	private List<IPathChangeListener> listeners;

	private Path latestPath;

	private static final String PATH_NAME = "Path";

	private PathManager(){
		listeners = new ArrayList<IPathChangeListener>();
		latestPath = null;
	}

	public static PathManager getInstance(){
		if(instance == null){
			instance = new PathManager();
		}
		return instance;
	}

	public void registerListener(IPathChangeListener listener){
		this.listeners.add(listener);
	}

	public void unregisterListener(IPathChangeListener listener){
		this.listeners.remove(listener);
	}

	public void setLatestPath(Path latestPath){
		//the path has changed, we should let everyone know
		this.latestPath = latestPath;
		for(IPathChangeListener listener : listeners){
			listener.onPathChange(latestPath);
		}
	}

	public Path getLatestPath() {
		return latestPath;
	}

	private String writePath(Path path) throws IOException {
		UUID id = UUID.randomUUID();


		File file = new File("paths/"+id.toString()+".json");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(path.toJsonString());
		bw.close();

		return id.toString();
	}



	private Path readPath(String id) throws FileNotFoundException, IOException, JsonParseException, JsonMappingException{
		try(BufferedReader br = new BufferedReader(new FileReader("paths/" + id + ".json"))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();

    		ObjectMapper mapper = new ObjectMapper();

    		Path path = mapper.readValue(everything, Path.class);

    		return path;

		}
	}

	public com.team254.lib.trajectory.Path makeChezyPath(HistoryBundle bundle) throws IOException {
		DriveConfig config =ConfigManager.getInstance().getConfig();


		WaypointSequence sequence = bundle.getWaypointList().toWaypointSequence();

		com.team254.lib.trajectory.Path path = PathGenerator.makePath(sequence, config.toChezyConfig(), config.getWidth(), PATH_NAME);


		Path bobPath = new Path(path);


		if(bundle.getWaypointList().isCachable()){
			String id = writePath(bobPath);

			PathHistory.getInstance().putPath(bundle, id);
		}

		setLatestPath(bobPath);

		return path;
	}

	public void getStoredPath(HistoryBundle bundle) throws JsonParseException, JsonMappingException, FileNotFoundException, JsonProcessingException, IOException{
		PathManager.getInstance().setLatestPath(readPath(PathHistory.getInstance().getId(bundle)));
	}

}
