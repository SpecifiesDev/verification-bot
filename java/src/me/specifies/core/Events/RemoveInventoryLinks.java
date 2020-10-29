package me.specifies.core.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import me.specifies.core.Verification;

public class RemoveInventoryLinks implements Listener {
	
	@EventHandler
	public void close(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if(Verification.inManagedInventory.containsKey(p.getUniqueId())) Verification.inManagedInventory.remove(p.getUniqueId());
	}

}
