package me.specifies.core.Requests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ApiTesting {
	
	private JsonParser parser = new JsonParser();
	private Constants consts = new Constants();
	
	public JsonObject APIStatus() throws Exception, SocketTimeoutException {
		
		HttpURLConnection conn = consts.newGetConnection("/status");
		
		conn.setConnectTimeout(7000);
		
		
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
