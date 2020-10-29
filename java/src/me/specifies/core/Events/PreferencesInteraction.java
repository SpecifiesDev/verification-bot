package me.specifies.core.Events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import me.specifies.core.Verification;
import me.specifies.core.Constants.PlayerLogging;
import me.specifies.core.Inventories.ItemFactory;
import me.specifies.core.Requests.PlayerPreferences;

public class PreferencesInteraction implements Listener {
	
	private Verification plugin;
	private PlayerPreferences preferences;
	public PreferencesInteraction() { 
		this.plugin = Verification.getInstance();
		this.preferences = new PlayerPreferences();
	}
	
	@EventHandler
	public void interact(InventoryClickEvent e) {
		
		Player p = (Player) e.getWhoClicked();
		
		if(Verification.inManagedInventory.containsKey(p.getUniqueId())) {
			
			if(Verification.inManagedInventory.get(p.getUniqueId()).equalsIgnoreCase("preferences")) {
				
				ItemStack clicked = e.getCurrentItem();
				Inventory inv = e.getInventory();
				
				if(e.getRawSlot() == 36) {
					p.closeInventory();
					Verification.inManagedInventory.remove(p.getUniqueId());
				} else if(e.getRawSlot() == 44) {
					
					// Grab all of the set objects in the inventory, evaluate if they're set to on or off, then convert them to an integer.
					int status = (inv.getItem(19).getDurability() == 13) ? 1 : 0;
					int message = (inv.getItem(22).getDurability() == 13) ? 1 : 0;
					int chat = (inv.getItem(25).getDurability() == 13) ? 1 : 0;
					
					
					try {
						JsonObject updatedResponse = preferences.setPreferences(status, chat, message, p.getUniqueId().toString());
						
						if(!updatedResponse.get("success").getAsBoolean()) {
							p.sendMessage(plugin.color(PlayerLogging.INTERNAL_ERROR));
							p.closeInventory();
							Verification.inManagedInventory.remove(p.getUniqueId());
							return;
						}
						
						p.closeInventory();
						Verification.inManagedInventory.remove(p.getUniqueId());
						p.sendMessage(plugin.color(PlayerLogging.PREF_UPDATED));
						
					} catch(Exception err) {
						err.printStackTrace();
						p.sendMessage(plugin.color(PlayerLogging.LOCAL_INTERNAL_ERROR));
						p.closeInventory();
						Verification.inManagedInventory.remove(p.getUniqueId());
					}
					
					
				} else {
					
					// This method of doing things makes the dynamic changing a lot easier to handle. If we add more preferences down the road, we won't have to do anything to this chunk of code.
					
					// Parse the title for the string 
					String title = ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).split(":")[0];
					
					if(clicked.getDurability() == 13) {
						
						ItemFactory factory = new ItemFactory(Material.AIR, 1);
						factory.flushShort(Material.STAINED_CLAY, 1, 6);
						
						factory.setDisplayName(plugin.color("&7" + title + "&8: &cOff."));
						
						p.getOpenInventory().setItem(e.getRawSlot(), factory.getItem());
						
						
					} else {
						
						ItemFactory factory = new ItemFactory(Material.AIR, 1);
						factory.flushShort(Material.STAINED_CLAY, 1, 13);
						
						factory.setDisplayName(plugin.color("&7" + title + "&8: &aOn."));
						
						p.getOpenInventory().setItem(e.getRawSlot(), factory.getItem());
						
					}
					
				}
				
			}
			
		}
		
	}

}
