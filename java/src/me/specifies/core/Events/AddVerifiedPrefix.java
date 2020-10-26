package me.specifies.core.Events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.google.gson.JsonObject;

import me.specifies.core.Verification;
import me.specifies.core.Requests.BotQuerying;
import me.specifies.core.Requests.PlayerLinking;
import me.specifies.core.Requests.PlayerPreferences;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;


/*
 * Event to modify a player's chat, indicating if they're verified or not.
 * This needs to be stress tested. In the local dev environment, there was noticeable delay of about .1ms.
 * With that being said, a lot of my resource usage was being used in other tasks. Pretty certain it won't need optimization,
 * but I need to stress test it just to be sure.
 */
public class AddVerifiedPrefix implements Listener {
	
	private Verification plugin;
	private PlayerLinking linking;
	private PlayerPreferences preferences;
	private BotQuerying bot;
	
	public AddVerifiedPrefix() {
		this.plugin = Verification.getInstance();
		this.linking = new PlayerLinking();
		this.preferences = new PlayerPreferences();
		this.bot = new BotQuerying();
	}
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent e) {
		
		Player p = e.getPlayer();
		
		String uuid = p.getUniqueId().toString();
		
		try {
			
			// first checked if the player has the preference toggled.
			JsonObject preferencesResponse = preferences.getPreferences(uuid);
			
			if(preferencesResponse.get("success").getAsBoolean()) {
				
				int toggled = preferencesResponse.get("chat").getAsInt();
				
				// It's off, just return.
				if(toggled == 0) return;
				
				// Check if the player is linked
				JsonObject checkLinked = linking.checkLinked(uuid);
				
				// player isn't linked, so no chat modication should occur
				if(!checkLinked.get("success").getAsBoolean()) return;
				
				// Get the discriminator of the player
				JsonObject botData = bot.getTag(uuid);
				
				// if for whatever reason there was no success, just return
				if(!botData.get("success").getAsBoolean()) return;
				
				String account = botData.get("tag").getAsString();
				
				// grab the original message, and strip it of color (prevents any other plugins from making chat look weird)
				String message = ChatColor.stripColor(e.getMessage());
				
				// Go through and construct a text component, monidfy it to contain the text, etc
				TextComponent serializedMessage = new TextComponent("");
				
				// Create a our prefix component, and add the events we want
				TextComponent verificationPrefix = new TextComponent(plugin.color("&8{&a\u2713&8} &7"));
				verificationPrefix.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(plugin.color("&7Verified Discord Account&8:\n&a" + account)).create()));
				
				// QoL feature. Places the text in the player's text box
				verificationPrefix.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, account));
				
				
				// Format our original message into a desire chat format add idea to #features regarding a possible change allowing server owners to toggle, or customize this.
				TextComponent formattedMessage = new TextComponent(plugin.color("&7" + p.getName() + " >> " + message));
				// We don't want this part of the text to be hoverable
				formattedMessage.setHoverEvent((HoverEvent) null);
				
				serializedMessage.addExtra(verificationPrefix);
				serializedMessage.addExtra(formattedMessage);
				
				// cancel the initial chat event, and then send the message globally
				
				e.setCancelled(true);
				
				for(Player player : Bukkit.getOnlinePlayers()) player.spigot().sendMessage(serializedMessage);
				
			}
			
		} catch(Exception err) {
			// Because this is a silent function there's no need for player logging
			err.printStackTrace();
		}
		
	}

}
