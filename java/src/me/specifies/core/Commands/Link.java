package me.specifies.core.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import me.specifies.core.Verification;
import me.specifies.core.Requests.PlayerLinking;

public class Link implements CommandExecutor {
	
	private Verification plugin;
	private PlayerLinking handler;
	private String internal_error;
	
	public Link() {
		this.plugin = Verification.getInstance();
		this.handler = new PlayerLinking();
		this.internal_error = "&cAn internal error occured. Please contact an Administrator.";
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
					if(linked) p.sendMessage(plugin.color("&aYou have already verified your account to this server's discord."));
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
								
								p.sendMessage(plugin.color("&aYour account is now pending verification. You may now verify in the discord server using the code: &6" + code + "&a."));
								
							} else p.sendMessage(plugin.color(internal_error + " Code: LX51"));
							
						} else if(pending) {
							// If the account is already pending, simply just tell them it's pending and send them a code.
							String code = checkLinked.get("code").getAsString().replace("-", "&7-&6");
							p.sendMessage(plugin.color("&aYour account is already pending verification. You may verify it in the discord server using the code: &6" + code + "&a."));
							
						}
					}
				} else p.sendMessage(plugin.color(internal_error + " Code: LX41")); // error logging meant to be sent me. Essentially just if something goes wrong on the bot side of things
				
			} catch(Exception err) {
				err.printStackTrace();
			}
			
		} else sender.sendMessage(plugin.color("&cYou must be a player to use this command."));
		
		return true;
	}

}
