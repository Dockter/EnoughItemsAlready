package org.dyndns.pamelloes.EIA;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import net.minecraft.server.v1_6_R3.NBTBase;
import net.minecraft.server.v1_6_R3.NBTTagList;

import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class InventorySave {
	private static final InventorySave instance;
	
	private final File basedir;
	
	/**
	 * Gets the singleton instance for saving/loading inventories.
	 */
	public static InventorySave getInstance() {
		return instance;
	}
	
	static {
		instance = new InventorySave(EnoughItemsAlready.getInstance());
	}
	
	private InventorySave(Plugin plugin) {
		basedir = new File(plugin.getDataFolder(), "inventory-saves");
		basedir.mkdirs();
	}
	
	/**
	 * Save an inventory.
	 * 
	 * @param player The player to associate the save with.
	 * @param name The name of the file to save to.
	 * @param inventory The inventory to save.
	 * @param overwrite Whether or not to overwrite the save.
	 * @return true if successful, if overwrite is false and this returns false then
	 * it can be assumed that the save already exists and overwrite needs to be true
	 * to successfully save.
	 * @throws FileNotFoundException Should an issue occur while trying to create the save file.
	 */
	public boolean saveInventory(Player player, String name, PlayerInventory inventory, boolean overwrite) throws FileNotFoundException {
		File playerdir = new File(basedir, player.getName());
		playerdir.mkdir();
		File save = new File(playerdir, name + ".dat");
		if(save.exists() && !overwrite) return false;
		if(!(inventory instanceof org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryPlayer)) return false;
		org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryPlayer cip = (org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryPlayer) inventory;
		net.minecraft.server.v1_6_R3.PlayerInventory mcinv = cip.getInventory();
		net.minecraft.server.v1_6_R3.NBTTagList nbtl = new net.minecraft.server.v1_6_R3.NBTTagList();
		mcinv.a(nbtl);
		net.minecraft.server.v1_6_R3.NBTBase.a(nbtl, new DataOutputStream(new FileOutputStream(save)));
		return true;
	}
	
	/**
	 * Loads an inventory.
	 * 
	 * @param player The player that the inventory is associated with.
	 * @param name The name of the inventory save.
	 * @return The loaded inventory or null if the inventory could not be loaded.
	 * @throws FileNotFoundException Should an issue occur while trying to read from the save file.
	 * @throws ClassCastException Should the inventory file be invalid.
	 */
	public PlayerInventory loadInventory(Player player, String name) throws FileNotFoundException {
		if(!(player instanceof CraftPlayer)) return null;
		File playerdir = new File(basedir, player.getName());
		playerdir.mkdir();
		File save = new File(playerdir, name);
		if(!save.exists()) return null;
		NBTTagList nbtl = (NBTTagList) NBTBase.a(new DataInputStream(new FileInputStream(save)));
		net.minecraft.server.v1_6_R3.PlayerInventory mcinv = new net.minecraft.server.v1_6_R3.PlayerInventory(((CraftPlayer) player).getHandle());
		mcinv.b(nbtl);
		return new CraftInventoryPlayer(mcinv);
	}
	
	/**
	 * Deletes an inventory.
	 * 
	 * @param player The player that the inventory is associated with.
	 * @param name The name of the inventory save to delete.
	 */
	public void deleteInventory(Player player, String name) {
		File playerdir = new File(basedir, player.getName());
		playerdir.mkdir();
		File save = new File(playerdir, name);
		if(save.exists()) save.delete();
	}
	
	/**
	 * Gets a list of all the inventories associated with the player.
	 */
	public String[] getInventories(Player player) {
		File playerdir = new File(basedir, player.getName());
		playerdir.mkdir();
		return playerdir.list(new FilenameFilter() {
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith(".dat");
			}
		});
	}
}
