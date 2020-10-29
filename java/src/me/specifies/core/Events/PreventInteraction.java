package me.specifies.core.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.specifies.core.Verification;

public class PreventInteraction implements Listener {
	
	@EventHandler
	public void interact(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(Verification.inManagedInventory.containsKey(p.getUniqueId())) e.setCancelled(true);
	}

}
