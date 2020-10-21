package me.specifies.core.Proxy;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import me.specifies.core.Verification;
import me.specifies.core.Proxy.Handlers.GetConfiguredServer;

public class ProxyServer {
	
	private int port;
	private Verification plugin;
	
	private HttpServer proxy;
	
	public ProxyServer() {
		this.plugin = Verification.getInstance();
		
		this.port = plugin.getConfig().getInt("proxy-port");
	}
	
	public void startProxy() throws Exception {
		
		// initalize the object
		this.proxy = HttpServer.create(new InetSocketAddress(this.port), 0);
		
		// contexts
		this.proxy.createContext("/iproxy/setup/configuredserver", new GetConfiguredServer());
		
		// configs
		this.proxy.setExecutor(null);
		
		// start
		this.proxy.start();
		
	}
	
	public void stop() {
		try { this.proxy.stop(0); } catch(Exception err) { err.printStackTrace(); }
	}

}
