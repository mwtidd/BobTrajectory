package com.team319.trajectory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.HistoryBundle;

public class TrajectoryHistory {
	private Map<String, String> trajectoryMap = new HashMap<String, String>();

	private static TrajectoryHistory instance;

	public static TrajectoryHistory getInstance(){
		if(instance == null){
			instance = new TrajectoryHistory();
		}
		return instance;
	}

	public boolean hasBundle(HistoryBundle bundle) throws JsonProcessingException{
		return trajectoryMap.containsKey(bundle.toJsonString());
	}

	public String getId(HistoryBundle bundle) throws JsonProcessingException{
		return trajectoryMap.get(bundle.toJsonString());
	}

	public void putTrajectory(HistoryBundle trajectoryBundle, String id) throws IOException {
		trajectoryMap.put(trajectoryBundle.toJsonString(), id);
		saveHistory();
	}

	private void saveHistory() throws IOException {

		File file = new File("trajectories/history.json");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		ObjectMapper mapper = new ObjectMapper();
		bw.write(mapper.writeValueAsString(trajectoryMap));
		bw.close();
	}

	public void loadHistory() throws FileNotFoundException, IOException, JsonParseException, JsonMappingException{
		File file = new File("trajectories/history.json");
		if(!file.exists())
			return;

		try(BufferedReader br = new BufferedReader(new FileReader("trajectories/history.json"))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();

    		ObjectMapper mapper = new ObjectMapper();

    		trajectoryMap = mapper.readValue(everything, HashMap.class);

		}
	}




}
