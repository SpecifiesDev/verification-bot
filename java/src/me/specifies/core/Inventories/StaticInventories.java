package me.specifies.core.Inventories;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import me.specifies.core.Verification;
import me.specifies.core.Constants.InventoryMessages;

public class StaticInventories {
	
	private Verification plugin;
	public StaticInventories() { this.plugin = Verification.getInstance(); }
	
	public Inventory playerPreferences(HashMap<String, Boolean> preferences) {
		
		Inventory inv = Bukkit.createInventory(null, 45, plugin.color(InventoryMessages.PREFERENCES_TITLE));
		
		ItemFactory offFactory = new ItemFactory(Material.STAINED_CLAY, 1);
		offFactory.flushShort(Material.STAINED_CLAY, 1, 6);
		
		ItemFactory onFactory = new ItemFactory(Material.STAINED_CLAY, 1);
		onFactory.flushShort(Material.STAINED_CLAY, 1, 13);
		
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
		
		endings.flushShort(Material.STAINED_GLASS, 1, 6);
		
		endings.setDisplayName("&c&lClose Preferences");
		
		inv.setItem(36, endings.getItem());
		
		
		return inv;
	}

}
