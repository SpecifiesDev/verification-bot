package me.specifies.core.Requests;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.specifies.core.Proxy.JSONFactory;

public class PlayerPreferences {
	
	private JsonParser parser = new JsonParser();
	private Constants consts = new Constants();
	
	public JsonObject getPreferences(String UUID) throws Exception, SocketTimeoutException {
		
		HttpURLConnection conn =  consts.newGetConnection("/preferences/player/" + UUID);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		String input;
		StringBuffer resp = new StringBuffer();
		
		while((input = in.readLine()) != null) {
			resp.append(input);
		}
		
		in.close();
		
		JsonElement elem = parser.parse(resp.toString());
		
		return elem.getAsJsonObject();
	}
	
	public JsonObject setPreferences(int status, int chat, int message, String UUID) throws Exception, SocketTimeoutException {
		
		HttpURLConnection conn = consts.newPostConnection("/preferences/players/set");
		
		// Construct the body
		JSONFactory factory = new JSONFactory();
		
		factory.putMultiple(new String[] {"UUID", UUID, "token", consts.auth, "server", consts.id, "status", Integer.toString(status)});
		factory.putMultiple(new String[] {"chat", Integer.toString(chat), "message", Integer.toString(message)});
		
		String body = factory.stringify();
		
		DataOutputStream write = new DataOutputStream(conn.getOutputStream());
		write.writeBytes(body);
		write.flush();
		write.close();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		String input;
		StringBuffer resp = new StringBuffer();
		
		while((input = in.readLine()) != null) {
			resp.append(input);
		}
		
		in.close();
		
		JsonElement elem = parser.parse(resp.toString());
		
		return elem.getAsJsonObject();
		
	}

}
