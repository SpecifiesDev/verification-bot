package me.specifies.core.Requests;

import java.net.HttpURLConnection;
import java.net.URL;

import me.specifies.core.Verification;

public class Constants {
	
	private String api = "http://localhost:8001/api/v1";
	private String agent = "Mozilla/5.0";
	
	public String auth;
	public String id;
	
	public Constants() {
		auth = Verification.getInstance().getConfig().getString("api-token");
		id =  Verification.getInstance().getConfig().getString("discord-server-id");
	}
	
	
	public HttpURLConnection newGetConnection(String endpoint) throws Exception {
		
		URL obj = new URL(api + endpoint);
		
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		
		conn.setRequestMethod("GET");;
		
		conn.setRequestProperty("token", auth);
		conn.setRequestProperty("server", id);
		conn.setRequestProperty("User-Agent", agent);
		
		conn.setConnectTimeout(5000);
		
		return conn;
	}
	
	public HttpURLConnection newPostConnection(String endpoint) throws Exception {
		
		URL obj = new URL(api + endpoint);
		
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		
		conn.setRequestMethod("POST");
		conn.setRequestProperty("User-Agent", agent);
		
		conn.setRequestProperty("mode", "cors");
		conn.setRequestProperty("cache", "no-cache");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("redirect", "follow");
		conn.setRequestProperty("referrer", "no-referrer");
		
		conn.setConnectTimeout(5000);
		
		conn.setDoOutput(true);
		
		return conn;
	}

}
