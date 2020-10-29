package me.specifies.core.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import me.specifies.core.Verification;
import me.specifies.core.Constants.PlayerLogging;
import me.specifies.core.Requests.PlayerLinking;

public class Link implements CommandExecutor {
	
	private Verification plugin;
	private PlayerLinking handler;
	
	public Link() {
		this.plugin = Verification.getInstance();
		this.handler = new PlayerLinking();
	}
	

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// Ensure that the command issuer is a player
		if(sender instanceof Player) {
			
			// Cast player to the sender
			Player p = (Player) sender;
			
			// wrap it all in try statements, may change it this being done within the methods to make more precise error logging
			try {
				// Send a request to check if the player is linked.
				JsonObject checkLinked = handler.checkLinked(p.getUniqueId().toString());
				
				// pull the base values that will be in the response.
				boolean success = checkLinked.get("success").getAsBoolean();
				boolean linked = checkLinked.get("linked").getAsBoolean();
				
				// if the request was a success, continue, if not notify the player
				if(success) {
					// if the player is already linked, notify them
					if(linked) p.sendMessage(plugin.color(PlayerLogging.VERIFIED));
					else {
						// grab the pending value from the request
						boolean pending = checkLinked.get("pending").getAsBoolean();
						
						// if the account isn't linked, and isn't pending, it isn't in the database. Means we need to add them.
						if(!linked && !pending) {
							
							// response object from the request to set status
							JsonObject setPending = handler.setPending(p.getUniqueId().toString());
							
							boolean pendingSuccess = setPending.get("success").getAsBoolean();
							
							// if it's a success, format the sent code then send them a message.
							if(pendingSuccess) {
								
								String code = setPending.get("code").getAsString().replace("-", "&7-&6");
								
								p.sendMessage(plugin.color(PlayerLogging.CREATED_CODE + code + "&a."));
								
							} else p.sendMessage(plugin.color(PlayerLogging.INTERNAL_ERROR + " Code: LX51"));
							
						} else if(pending) {
							// If the account is already pending, simply just tell them it's pending and send them a code.
							String code = checkLinked.get("code").getAsString().replace("-", "&7-&6");
							p.sendMessage(plugin.color(PlayerLogging.PENDING + code + "&a."));
							
						}
					}
				} else p.sendMessage(plugin.color(PlayerLogging.INTERNAL_ERROR + " Code: LX41")); // error logging meant to be sent me. Essentially just if something goes wrong on the bot side of things
				
			} catch(Exception err) {
				err.printStackTrace();
				p.sendMessage(plugin.color(PlayerLogging.LOCAL_INTERNAL_ERROR));
			}
			
		} else sender.sendMessage(plugin.color(PlayerLogging.PLAYER_ONLY));
		
		return true;
	}

}
