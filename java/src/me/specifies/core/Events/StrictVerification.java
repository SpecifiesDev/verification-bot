package me.specifies.core.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.gson.JsonObject;

import me.specifies.core.Verification;
import me.specifies.core.Constants.ErrorLogging;
import me.specifies.core.Constants.PlayerLogging;
import me.specifies.core.Requests.PlayerLinking;

public class StrictVerification implements Listener {
	
	private Verification plugin;
	private PlayerLinking handler;
	public StrictVerification() {
		this.plugin = Verification.getInstance();
		this.handler = new PlayerLinking();
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		
		// Grab an instance of the player for data
		Player p = e.getPlayer();
		
		// Pre format the configured discord link
		String formattedDisc = plugin.getConfig().getString("discord-link").replace(":", "&c:&7");
		
		// check if strict verification is toggled
		if(plugin.getConfig().getBoolean("strict-verification")) {
			
			// surround in a try / catch block
			try {
				
				// request to check if they're linked
				JsonObject checkLinked = handler.checkLinked(p.getUniqueId().toString());
				
				boolean success = checkLinked.get("success").getAsBoolean();
				boolean linked = checkLinked.get("linked").getAsBoolean();
				
				// if not success, just skip over the entire function. This implemented this way as to not prevent server functionality in the event that the api is down.
				if(!success) {
					Bukkit.getConsoleSender().sendMessage(plugin.color(ErrorLogging.STRICT_VERIFICATION)); 
					return;
				}
				
				// if they're linked, just ignore
				if(linked) return;
				
				
				boolean pending = checkLinked.get("pending").getAsBoolean();
				
				// if not linked, and not pending, they need to be added to the database
				
				if(!linked && !pending) {
					JsonObject setPending = handler.setPending(p.getUniqueId().toString());
					
					boolean pendingSuccess = setPending.get("success").getAsBoolean();
					
					if(!pendingSuccess) {
						Bukkit.getConsoleSender().sendMessage(plugin.color(ErrorLogging.STRICT_VERIFICATION)); 
						return;
					}
					
					String code = setPending.get("code").getAsString().replace("-", "&7-&6");
					
					// Kick the player with the formatted message
					p.kickPlayer(plugin.color(PlayerLogging.CREATED_CODE_STRICT + code + "&7\n" + formattedDisc));
					
				} else if(pending) {
					
					String code = checkLinked.get("code").getAsString().replace("-", "&7-&6");
					
					// Kick the player with the formatted message
					p.kickPlayer(plugin.color(PlayerLogging.PENDING_STRICT + code + "&7\n" + formattedDisc));
					
				}
				
				
				
			} catch(Exception err) {
				// Error logging. Key thing here is that if an error is fired, strict verification is basically disabled to prevent major issues.
				err.printStackTrace();
				Bukkit.getConsoleSender().sendMessage(plugin.color(ErrorLogging.STRICT_VERIFICATION));
				
			}
			
		}
		
		
	}

}
