package me.specifies.core.Inventories;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.specifies.core.Verification;

public class ItemFactory {
	
	private ItemStack stack;
	private Verification plugin;
	public ItemFactory(Material m, int amount) {
		this.stack = new ItemStack(m, amount);
		this.plugin = Verification.getInstance();
	}
	
	public ItemStack getItem() {
		
		return this.stack;
		
	}
	
	private ItemMeta getMeta() {
		
		return this.stack.getItemMeta();
		
	}

	
	public void flushShort(Material m, int amount, int data) {
		
		this.stack = new ItemStack(m, amount, (short) data);
		
	}
	
	public void setType(Material mat) {
		
		this.stack.setType(mat);
		
	}
	
	public void setDisplayName(String name) {
		
		ItemMeta im = this.getMeta();
		
		im.setDisplayName(this.plugin.color(name));
		
		this.stack.setItemMeta(im);
		
	}
	
	public void constructLore(String additive) {
		
		ItemMeta im = this.getMeta();
		
		ArrayList<String> construct = new ArrayList<>(im.getLore());
		
		for(String s : additive.split("~")) construct.add(this.plugin.color(s));
		
		im.setLore(construct);
		
	}
	
	public void setLore(String lore) {
		
		ItemMeta im = this.getMeta();
		
		im.setLore(Arrays.asList(lore.split("~")));
		
		this.stack.setItemMeta(im);
		
	}
	
	public void flush(Material mat, int amount) {
		this.stack = new ItemStack(mat, amount);
	}
	
	
	
	

}
