package me.specifies.core.Proxy.Handlers;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import me.specifies.core.Verification;
import me.specifies.core.Proxy.JSONFactory;

public class GetConfiguredServer implements HttpHandler {
	
	public void handle(HttpExchange exc) throws IOException {
		
		
		
		// create our response object to build onto
		byte[] response = null;
		
		// create a json factory to build our response json with
		JSONFactory factory = new JSONFactory();
		
		// get an instance of our plugin to grab the data we need to send
		Verification plugin = Verification.getInstance();
		
		// put our data in the factory
		factory.putMultiple(new String[] {"success", "true", "id", plugin.getConfig().getString("discord-server-id")});
		
		
		// build a response from our factory
		response = factory.stringify().getBytes();
		
		// set the appropriate headers
		exc.sendResponseHeaders(200, response.length);
		
		// Send the data
		OutputStream stream = exc.getResponseBody();
		stream.write(response);
		stream.close();
	}

}
