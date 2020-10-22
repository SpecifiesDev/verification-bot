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
		
		if(sender instanceof Player) {
			
			Player p = (Player) sender;
			
			try {
				System.out.println(1);
				JsonObject checkLinked = handler.checkLinked(p.getUniqueId().toString());
				
				boolean success = checkLinked.get("success").getAsBoolean();
				boolean linked = checkLinked.get("linked").getAsBoolean();
				
				System.out.println(2);
				System.out.println(linked);
				
				if(success) {
					if(linked) p.sendMessage(plugin.color("&aYou have already verified your account to this server's discord."));
					else {
						boolean pending = checkLinked.get("pending").getAsBoolean();
						if(!linked && !pending) { // Isn't in database
							
							JsonObject setPending = handler.setPending(p.getUniqueId().toString());
							
							boolean pendingSuccess = setPending.get("success").getAsBoolean();
							
							if(pendingSuccess) {
								
								String code = setPending.get("code").getAsString().replace("-", "&7-&6");
								
								p.sendMessage(plugin.color("&aYour account is now pending verification. You may now verify in the discord server using the code: &6" + code + "&a."));
								
							} else p.sendMessage(plugin.color(internal_error + " Code: LX51"));
							
						} else if(pending) {
							
							String code = checkLinked.get("code").getAsString().replace("-", "&7-&6");
							p.sendMessage(plugin.color("&aYour account is already pending verification. You may verify it in the discord server using the code: &6" + code + "&a."));
							
						}
					}
				} else p.sendMessage(plugin.color(internal_error + " Code: LX41"));
				
			} catch(Exception err) {
				err.printStackTrace();
			}
			
		} else sender.sendMessage(plugin.color("&cYou must be a player to use this command."));
		
		return true;
	}

}
