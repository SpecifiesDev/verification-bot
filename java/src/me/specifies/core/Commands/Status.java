package me.specifies.core.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.specifies.core.Verification;
import me.specifies.core.Constants.InventoryMessages;
import me.specifies.core.Constants.PlayerLogging;
import me.specifies.core.Inventories.StaticInventories;
import me.specifies.core.Requests.BotQuerying;
import me.specifies.core.Requests.PlayerLinking;
import me.specifies.core.Requests.PlayerPreferences;

public class Status implements CommandExecutor {
	
	private Verification plugin;
	private BotQuerying statusQuery;
	private PlayerLinking linking;
	private PlayerPreferences preferences;
	private StaticInventories inventories;
	public Status() {
		this.plugin = Verification.getInstance();
		this.statusQuery = new BotQuerying();
		this.linking = new PlayerLinking();
		this.preferences = new PlayerPreferences();
		this.inventories = new StaticInventories();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {
			
			Player p = (Player) sender;
			
			if(args.length == 0) {
				handlePlayer(p, p);
			} else {
				Player target = Bukkit.getPlayer(args[0]);
				if(!(target == null)) handlePlayer(p, target);
				else p.sendMessage(plugin.color(PlayerLogging.STATUS_ONLINE));
			}
			
			
		} else sender.sendMessage(plugin.color(PlayerLogging.PLAYER_ONLY));
		
		return true;
	}
	
	private void handlePlayer(Player p, Player target) {
		
		// First check if linked
		try {
			
			JsonObject checkLinked = linking.checkLinked(target.getUniqueId().toString());
			
			if(checkLinked.get("linked").getAsBoolean()) {
				
				JsonObject checkToggled = preferences.getPreferences(target.getUniqueId().toString());
				int status = checkToggled.get("status").getAsInt();
				
				if(status == 1) {
					
					JsonObject info = statusQuery.getStatus(target.getUniqueId().toString());
					
					if(info.get("success").getAsBoolean()) {
						
						String songString = "";
						String onlineState = info.get("status").getAsString();
						String gameString = "";
						String customString = "";
						
						JsonElement song = info.get("song");
						JsonElement game = info.get("game");
						JsonElement custom = info.get("custom");
						
						if(song == null) songString = InventoryMessages.SONG_DEFAULT;
						else songString = song.getAsString();
						
						if(game == null) gameString = InventoryMessages.GAME_DEFAULT;
						else gameString = game.getAsString();
						
						if(!(custom == null)) customString = custom.getAsString();
						
						
						p.openInventory(inventories.playerStatus(songString, onlineState, gameString, customString, target.getName()));
						Verification.inManagedInventory.put(p.getUniqueId(), "status");
						
						
						
					} else p.sendMessage(plugin.color(PlayerLogging.INTERNAL_ERROR));
					
				} else p.sendMessage(plugin.color(PlayerLogging.STATUS_OFF));
				
			} else p.sendMessage(plugin.color(PlayerLogging.STATUS_NO_LINK));
			
		} catch (Exception err) {
			err.printStackTrace();
			p.sendMessage(PlayerLogging.LOCAL_INTERNAL_ERROR);
		}
		
	}
}
