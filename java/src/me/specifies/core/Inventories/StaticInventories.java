package me.specifies.core.Inventories;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import me.specifies.core.Verification;
import me.specifies.core.Constants.InventoryMessages;

public class StaticInventories {
	
	private Verification plugin;
	public StaticInventories() { this.plugin = Verification.getInstance(); }
	
	public Inventory playerStatus(String song, String status, String game, String custom, String name) {
		
		Inventory inv = Bukkit.createInventory(null, 27, plugin.color(InventoryMessages.STATUS_TITLE + name));
		
		ItemFactory factory = new ItemFactory(Material.GREEN_TERRACOTTA, 1);
		
		if(status.equalsIgnoreCase("online")) {
			factory.setDisplayName("&a&lOnline");
		}
		if(status.equalsIgnoreCase("dnd")) {
			factory.setDisplayName("&c&lDo Not Disturb");
			factory.setType(Material.RED_TERRACOTTA);
		}
		if(status.equalsIgnoreCase("idle")) {
			factory.setDisplayName("&e&lAway");
			factory.setType(Material.YELLOW_TERRACOTTA);
		}
		if(status.equalsIgnoreCase("offline")) {
			factory.setDisplayName("&8&lOffline");
			factory.setType(Material.GRAY_TERRACOTTA);
		}
		
		inv.setItem(13, factory.getItem());
		
		
		if(!custom.equals("")) {
			factory.setType(Material.PAPER);
			factory.setDisplayName("&cStatus &8- &c" + custom);
			inv.setItem(22, factory.getItem());
		}
		
		
		
		factory.setType(Material.NOTE_BLOCK);
		
		factory.setDisplayName("&cListening To &8- &c" + song);
		
		inv.setItem(11, factory.getItem());
		
		factory.setType(Material.DIAMOND_SWORD);
		
		factory.setDisplayName("&cPlaying &8- &c" + game);
		
		factory.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
		
		inv.setItem(15, factory.getItem());
		
		return inv;
		
	}
	
	public Inventory playerPreferences(HashMap<String, Boolean> preferences) {
		
		Inventory inv = Bukkit.createInventory(null, 45, plugin.color(InventoryMessages.PREFERENCES_TITLE));
		
		ItemFactory offFactory = new ItemFactory(Material.RED_TERRACOTTA, 1);
		
		ItemFactory onFactory = new ItemFactory(Material.GREEN_TERRACOTTA, 1);	
		// query status
		if(preferences.get("status")) {
			onFactory.setDisplayName(InventoryMessages.STATUS_ON);
			
			inv.setItem(19, onFactory.getItem());
			
		} else {
			offFactory.setDisplayName(InventoryMessages.STATUS_OFF);
			
			inv.setItem(19,  offFactory.getItem());
		}
		
		if(preferences.get("message")) {
			onFactory.setDisplayName(InventoryMessages.MESSAGE_ON);
			
			inv.setItem(22, onFactory.getItem());
		} else {
			offFactory.setDisplayName(InventoryMessages.MESSAGE_OFF);
			
			inv.setItem(22,  offFactory.getItem());
		}
		
		if(preferences.get("chat")) {
			onFactory.setDisplayName(InventoryMessages.CHAT_ON);
			
			inv.setItem(25, onFactory.getItem());
		} else {
			offFactory.setDisplayName(InventoryMessages.CHAT_OFF);
			
			inv.setItem(25, offFactory.getItem());
		}
		
		ItemFactory endings = new ItemFactory(Material.ARROW, 1);
		
		endings.setDisplayName("&a&lSave Preferences");
		
		inv.setItem(44, endings.getItem());
		
		endings.flush(Material.RED_DYE, 1);
		
		endings.setDisplayName("&c&lExit");
		
		inv.setItem(36, endings.getItem());
		
		
		return inv;
	}

}
