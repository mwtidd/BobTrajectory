package com.team319.path;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team319.HistoryBundle;

public class PathHistory {
	private Map<String, String> pathMap = new HashMap<String, String>();

	private static PathHistory instance;

	public static PathHistory getInstance(){
		if(instance == null){
			instance = new PathHistory();
		}
		return instance;
	}

	public boolean hasBundle(HistoryBundle bundle) throws JsonProcessingException{
		return pathMap.containsKey(bundle.toJsonString());
	}

	public String getId(HistoryBundle bundle) throws JsonProcessingException{
		return pathMap.get(bundle.toJsonString());
	}

	public void putPath(HistoryBundle trajectoryBundle, String id) throws IOException {
		pathMap.put(trajectoryBundle.toJsonString(), id);
		saveHistory();
	}

	private void saveHistory() throws IOException {

		File file = new File("paths/history.json");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		ObjectMapper mapper = new ObjectMapper();
		bw.write(mapper.writeValueAsString(pathMap));
		bw.close();
	}

	public void loadHistory() throws FileNotFoundException, IOException, JsonParseException, JsonMappingException{
		File file = new File("paths/history.json");
		if(!file.exists())
			return;

		try(BufferedReader br = new BufferedReader(new FileReader("paths/history.json"))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();

    		ObjectMapper mapper = new ObjectMapper();

    		pathMap = mapper.readValue(everything, HashMap.class);

		}
	}




}
