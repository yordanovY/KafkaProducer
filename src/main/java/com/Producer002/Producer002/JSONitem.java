package com.Producer002.Producer002;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONitem {

	private String sender;
	private String destination;
	private String server;
	private String content;
	
	public JSONitem(File x) throws IOException {
		super();
		
		String jsonString = FileUtils.readFileToString(x, Charset.defaultCharset());
		JSONObject obj = new JSONObject(jsonString);
		//information for header
		this.sender = obj.getJSONObject("recieverInformation").getString("sender");
		this.destination = obj.getJSONObject("recieverInformation").getString("destination");
		this.server = obj.getJSONObject("recieverInformation").getString("server");
		//this.content = FileUtils.readFileToString(x, Charset.defaultCharset());

		JSONArray arr = obj.getJSONArray("posts"); // notice that `"posts": [...]`
		for (int i = 0; i < arr.length(); i++)
		{
		    String post_id = arr.getJSONObject(i).toString();
		    content+=post_id+"\n";
		}
		
	}

	//getter for sender
	public String getSender() {
		return sender;
	}
	
	//setting for sender
	public void setSender(String sender) {
		this.sender = sender;
	}

	//getter for destination
	public String getDestination() {
		return destination;
	}

	//setter for sender
	public void setDestination(String destination) {
		this.destination = destination;
	}

	//getter for server
	public String getServer() {
		return server;
	}

	//setter for server
	public void setServer(String server) {
		this.server = server;
	}

	//getter for content
	public String getContent() {
		return content;
	}

	//setter for content
	public void setContent(String content) {
		this.content = content;
	}
	
	
	
}
