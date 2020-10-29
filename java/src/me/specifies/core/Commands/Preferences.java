package me.specifies.core.Commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.google.gson.JsonObject;

import me.specifies.core.Verification;
import me.specifies.core.Constants.PlayerLogging;
import me.specifies.core.Inventories.StaticInventories;
import me.specifies.core.Requests.PlayerLinking;
import me.specifies.core.Requests.PlayerPreferences;

public class Preferences implements CommandExecutor {

	private Verification plugin;
	private PlayerLinking linking;
	private PlayerPreferences preferences;
	private StaticInventories factory;
	
	public Preferences() {
		this.plugin = Verification.getInstance();
		this.linking = new PlayerLinking();
		this.preferences = new PlayerPreferences();
		this.factory = new StaticInventories();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {
			
			Player p = (Player) sender;
			
			try {
				// check if they're linked
				JsonObject checkLinked = linking.checkLinked(p.getUniqueId().toString());
				
				if(checkLinked.get("linked").getAsBoolean()) {
					
					HashMap<String, Boolean> preferencesMap = new HashMap<String, Boolean>();
					
					JsonObject prefs = preferences.getPreferences(p.getUniqueId().toString());
					
					preferencesMap.put("status", (prefs.get("status").getAsInt() == 1));
					preferencesMap.put("message", (prefs.get("message").getAsInt() == 1));
					preferencesMap.put("chat", (prefs.get("chat").getAsInt() == 1));
					
					Inventory built = factory.playerPreferences(preferencesMap);
					
					if(!Verification.inManagedInventory.containsKey(p.getUniqueId())) {
						Verification.inManagedInventory.put(p.getUniqueId(), "preferences");
						p.openInventory(built);
					} else {
						p.sendMessage(plugin.color(PlayerLogging.LOCAL_INTERNAL_ERROR));
					}
					
				} else {
					p.sendMessage(plugin.color(PlayerLogging.PREF_NO_LINK));
				}
				
			} catch(Exception err) {
				p.sendMessage(plugin.color(PlayerLogging.LOCAL_INTERNAL_ERROR));
				err.printStackTrace();
			}
			
		} else sender.sendMessage(plugin.color(PlayerLogging.PLAYER_ONLY));
		
		
		return true;
	}
	
}
