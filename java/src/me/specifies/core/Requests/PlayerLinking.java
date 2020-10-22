package me.specifies.core.Requests;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.specifies.core.Proxy.JSONFactory;

public class PlayerLinking {
	
	private JsonParser parser = new JsonParser();
	private Constants consts = new Constants();
	
	public JsonObject checkLinked(String UUID) throws Exception {
		
		HttpURLConnection conn =  consts.newGetConnection("/linking/status/" + UUID);
		
		
		// Create new eader
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		// Define string buffers to read the response
		String input;
		StringBuffer resp = new StringBuffer();
		
		// append response to buffer
		while((input = in.readLine()) != null) {
			resp.append(input);
		}
		
		in.close();
		
		// create json element, btw I really dislike gson :D
		JsonElement elem = parser.parse(resp.toString());
		
		return elem.getAsJsonObject();
	}
	
	public JsonObject setPending(String UUID) throws Exception {
		
		HttpURLConnection conn = consts.newPostConnection("/linking/setstatus/pending");
		
		
		JSONFactory factory = new JSONFactory();
		
		factory.putMultiple(new String[] {"UUID", UUID, "token", consts.auth, "server", consts.id});
		
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
