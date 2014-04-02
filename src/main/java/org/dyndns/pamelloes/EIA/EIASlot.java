package org.dyndns.pamelloes.EIA;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.GenericSlot;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Slot;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.material.MaterialData;

public class EIASlot extends GenericSlot {
	private Material mat;
	
	public EIASlot(Material mat) {
		setMaterial(mat);
	}
	
	@Override
	public Slot setItem(ItemStack is) {
		if(getScreen() != null && getScreen().getPlayer().getGameMode().equals(GameMode.CREATIVE) && (getScreen().getPlayer().getCurrentScreen().getScreenType().equals(ScreenType.PLAYER_INVENTORY) || getScreen().getPlayer().getCurrentScreen().getScreenType().equals(ScreenType.PLAYER_INVENTORY_CREATIVE))) Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
			public void run() {
				((CraftPlayer) getScreen().getPlayer()).getHandle().inventory.setCarried(CraftItemStack.createNMSItemStack(new ItemStack(0)));
			}
		}, 1L);
		return super.setItem(new SpoutItemStack(mat, 64));
	}
	
	public Material getMaterial() {
		return mat;
	}
	
	public void setMaterial(Material mat) {
		this.mat = mat;
		setItem(null);
	}
	
	@Override
	public boolean onItemExchange(ItemStack i1, ItemStack i2) {
		boolean recipe = EnoughItemsAlready.map.get(getScreen().getPlayer()).hasRecipeMode();
		if(recipe) {
			EIARecipeGui.showRecipes(new SpoutItemStack(mat, 1), getScreen().getPlayer());
			return false;
		}
		return super.onItemExchange(i1, i2);
	}
	
	@Override
	public boolean onItemTake(ItemStack i1) {
		boolean recipe = EnoughItemsAlready.map.get(getScreen().getPlayer()).hasRecipeMode();
		if(recipe) {
			EIARecipeGui.showRecipes(new SpoutItemStack(mat, 1), getScreen().getPlayer());
			return false;
		}
		return super.onItemTake(i1);
	}
	
	@Override
	public boolean onItemPut(ItemStack i1) {
		boolean recipe = EnoughItemsAlready.map.get(getScreen().getPlayer()).hasRecipeMode();
		if(recipe) {
			EIARecipeGui.showRecipes(new SpoutItemStack(mat, 1), getScreen().getPlayer());
			return false;
		}
		return super.onItemPut(i1);
	}
	
	@Override
	public void onItemShiftClicked() {
		if(getScreen().getPlayer().getItemOnCursor().getType().equals(mat) || getScreen().getPlayer().getItemOnCursor().getType().equals(MaterialData.air)) getScreen().getPlayer().setItemOnCursor(new SpoutItemStack(mat, 64));
	}
}
