package me.specifies.core.Proxy.Handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import me.specifies.core.Verification;
import me.specifies.core.Constants.InventoryMessages;
import me.specifies.core.Inventories.StaticInventories;
import me.specifies.core.Proxy.JSONFactory;

public class EmitStatusChange implements HttpHandler {
	
	public void handle(HttpExchange exc) throws IOException {
		
		// only accept post requests
		if(exc.getRequestMethod().equalsIgnoreCase("get")) return;
		
		JsonParser parser = new JsonParser();
		StaticInventories inventories = new StaticInventories();
		
		
		try {
			
			// parse the entire request
			Headers requestHeaders = exc.getRequestHeaders();
			
			Set<Map.Entry<String, List<String>>> entries = requestHeaders.entrySet();
			
			int length = Integer.parseInt(requestHeaders.getFirst("Content-length"));
			
			InputStream is = exc.getRequestBody();
			
			byte[] data = new byte[length];
			is.read(data);
			
			String body = new String(data);
			
			
			// send a response
			JSONFactory factory = new JSONFactory();
			factory.putMultiple(new String[] {"success", "true"});
			
			byte[] response = factory.stringify().getBytes();
			
			
			exc.sendResponseHeaders(200, response.length);
			
			OutputStream stream = exc.getResponseBody();
			stream.write(response);
			stream.close();
			
			JsonObject info = parser.parse(body).getAsJsonObject();
			
			// pull all of the status data that was emitted
				
			String songString = "";
			String onlineState = "";
			String gameString = "";
			String customString = "";
			String uuidString = "";
				
			JsonElement song = info.get("song");
			JsonElement game = info.get("game");
			JsonElement custom = info.get("custom");
			JsonElement status = info.get("status");
			JsonElement uuid = info.get("uuid");
				
			if(song == null) songString = InventoryMessages.SONG_DEFAULT;
			else songString = song.getAsString();
				
			if(game == null) gameString = InventoryMessages.GAME_DEFAULT;
			else gameString = game.getAsString();
				
			if(!(custom == null)) customString = custom.getAsString();
			
			if(uuid == null) return;
			else uuidString = uuid.getAsString();
			
			if(status == null) return;
			else onlineState = status.getAsString();
				
			for(Player p : Bukkit.getOnlinePlayers()) {
				
				// make sure they're in managed inventory
				if(Verification.inManagedInventory.containsKey(p.getUniqueId())) {
					
					// make sure the managed inventory is a type of status
					if(!(Verification.inManagedInventory.get(p.getUniqueId()).equals("status"))) return;
					
					// parse the name of the opened inventory to query the player's status
					String openName = ChatColor.stripColor(p.getOpenInventory().getTitle()).split("- ")[1];
					
					// grab an object of the emitted player
					OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(uuidString));
					
					// make sure the inv is equal to the emitted player
					if(target.getName().equalsIgnoreCase(openName)) {
						// get the open inventory
						Inventory inv = p.getOpenInventory().getTopInventory();
						
						/*
						 * So here we simply just want to change the object in the inventory. We don't want to close, or open a new inventory because
						 * of the way we handle inventory states. We'll simply construct an inventory object out of the new data, and pull the itemstacks
						 * from that inventory.
						 */
						
						// this'll be a hacky way of doing it but (:
						Inventory constructed = inventories.playerStatus(songString, onlineState, gameString, customString, target.getName());
						
						if(!customString.equals("")) inv.setItem(22, constructed.getItem(22));
						else inv.setItem(22, new ItemStack(Material.AIR, 1));
						
						inv.setItem(13, constructed.getItem(13));
						inv.setItem(11, constructed.getItem(11));
						inv.setItem(15, constructed.getItem(15));
						
					}
					
				}
				
			}
				
				
				
			
			
			
			
		} catch(Exception err) {
			err.printStackTrace();
			
			JSONFactory error = new JSONFactory();
			error.putMultiple(new String[] {"success", "false"});
			
			byte[] resp = error.stringify().getBytes();
			
			exc.sendResponseHeaders(500, resp.length);
			
			OutputStream stream = exc.getResponseBody();
			stream.write(resp);
			stream.close();
		}
		
	
	}

}
