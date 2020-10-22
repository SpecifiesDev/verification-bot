package me.specifies.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.specifies.core.Commands.Link;
import me.specifies.core.Proxy.ProxyServer;

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
	}
	
	public void onDisable() { 
		core = null; 
		server.stop();
	}
	
	private void registerEvents() {
		PluginManager pm = Bukkit.getPluginManager();
	}
	
	private void registerCommands() {
		getCommand("link").setExecutor(new Link());
	}
	
	public static Verification getInstance() {
		return core;
	}
	
	public String color(String m) {
		return ChatColor.translateAlternateColorCodes('&', m);
	}

}
