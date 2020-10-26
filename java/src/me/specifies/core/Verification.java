package me.specifies.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.specifies.core.Commands.Link;
import me.specifies.core.Constants.ErrorLogging;
import me.specifies.core.Events.AddVerifiedPrefix;
import me.specifies.core.Events.StrictVerification;
import me.specifies.core.Proxy.ProxyServer;
import me.specifies.core.Requests.ApiTesting;

public class Verification extends JavaPlugin {
	
	private static Verification core;
	private ProxyServer server;
	
	public void onEnable() {
		core = this;
	
		registerEvents();
		registerCommands();
		
		this.saveDefaultConfig();
		
		server = new ProxyServer();
		
		try { server.startProxy(); } catch(Exception err) { err.printStackTrace(); }
		
		// Make sure we test the api after all our setting up is done. If we don't, it will cause errors on disabling.
		testAPI();
	}
	
	public void onDisable() { 
		core = null; 
		server.stop();
	}
	
	public static Verification getInstance() {
		return core;
	}
	
	public String color(String m) {
		return ChatColor.translateAlternateColorCodes('&', m);
	}
	
	/*
	 * Private Methods
	 */
	
	// Register all events here, except for ones that we want to toggle
	private void registerEvents() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new StrictVerification(), this);
		
		
		// only register this event if the owner has this on
		if(this.getConfig().getBoolean("verified-prefix")) pm.registerEvents(new AddVerifiedPrefix(), this);
		
	}
	
	// Register all commands here
	private void registerCommands() {
		getCommand("link").setExecutor(new Link());
	}
	
	// Function to check the uptime of the API. 7 second timeout. If the server can't connect, or the socket times out, the plugin is disabled.
	private void testAPI() {
		ApiTesting tester = new ApiTesting();
		
		try {
			tester.APIStatus();
		} catch(Exception err) {
			Bukkit.getServer().getConsoleSender().sendMessage(this.color(ErrorLogging.UNABLE_TO_REACH_API));
			this.getServer().getPluginManager().disablePlugin(this);
		}
	}

}
