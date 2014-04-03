package org.dyndns.pamelloes.EIA;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.screen.ScreenOpenEvent;
import org.getspout.spoutapi.event.spout.ServerTickEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

public class EIAListener implements Listener {
	private int counter = 50;
	
	public EIAListener(EnoughItemsAlready plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onScreenOpen(final ScreenOpenEvent e) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(EnoughItemsAlready.getInstance(), new Runnable() {
			public void run() {
				if(e.isCancelled()) return;
				//Validate inventory screen.
				ScreenType st = e.getScreenType();
				if(!st.equals(ScreenType.CHEST_INVENTORY) && !st.equals(ScreenType.DISPENSER_INVENTORY) && !st.equals(ScreenType.FURNACE_INVENTORY)
						&& !st.equals(ScreenType.PLAYER_INVENTORY) && !st.equals(ScreenType.WORKBENCH_INVENTORY) && !st.equals(ScreenType.BREWING_STAND_INVENTORY)
						&& !st.equals(ScreenType.ENCHANTMENT_INVENTORY) && !st.equals(ScreenType.PLAYER_INVENTORY_CREATIVE)) return;
				//Validate permissions
				if(!e.getPlayer().hasPermission("eia.display")) return;
				//Create and display gui
				if(!EnoughItemsAlready.map.containsKey(e.getPlayer())) { 
					EnoughItemsAlready.map.put(e.getPlayer(), new EIAGui(e.getPlayer()));
				}
				EnoughItemsAlready.map.get(e.getPlayer()).attach(e.getPlayer().getCurrentScreen());
			}
		} );
	}
	
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent e) { //This is a dirty hack to get around the fact that the creative inventory doesn't send the item stack on the cursor to the server.
		Bukkit.getScheduler().scheduleSyncDelayedTask(EnoughItemsAlready.getInstance(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				SpoutPlayer sp = e.getWhoClicked() instanceof SpoutPlayer ? (SpoutPlayer) e.getWhoClicked() : null;
				if(sp == null) return;
				if(EnoughItemsAlready.map.get(sp) == null) return;
				if(EnoughItemsAlready.map.get(sp).hasClearMode() && e.getSlot() >= 0) {
					int slot = e.getView().convertSlot(e.getRawSlot());
					if(slot == e.getRawSlot()) e.getWhoClicked().getInventory().clear(slot);
					else e.getView().getBottomInventory().clear(slot);
					e.getWhoClicked().setItemOnCursor(new ItemStack(0));
				}
			}
		} );
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		EnoughItemsAlready.map.remove(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerKickEvent e) {
		EnoughItemsAlready.map.remove(e.getPlayer());
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		if (e.isCancelled()) return;
		for(Player p : e.getWorld().getPlayers()) {
			SpoutPlayer sp = SpoutManager.getPlayer(p);
			if(!EnoughItemsAlready.map.containsKey(sp)) continue;
			EIAGui gui = EnoughItemsAlready.map.get(sp);
			gui.updateWeather(e.toWeatherState());
		}
	}
	
	@EventHandler
	public void onTick(ServerTickEvent e) {
		if(counter % 25 == 1) for(Player p : Bukkit.getOnlinePlayers()) {
			SpoutPlayer sp = SpoutManager.getPlayer(p);
			if(!EnoughItemsAlready.map.containsKey(sp)) continue;
			EIAGui gui = EnoughItemsAlready.map.get(sp);
			gui.updateSearch();
		}
		if(--counter != 0) return;
		counter = 50;
		for(World w : Bukkit.getWorlds()) {
			int index = (int) (w.getTime()) % 24000;
			index = (index - (index % 6000)) / 6000;
			for(Player p : w.getPlayers()) {
				SpoutPlayer sp = SpoutManager.getPlayer(p);
				if(!EnoughItemsAlready.map.containsKey(sp)) continue;
				EIAGui gui = EnoughItemsAlready.map.get(sp);
				gui.updateTime(index);
				String biome = w.getBiome(sp.getLocation().getBlockX(), sp.getLocation().getBlockZ()).toString().toLowerCase();
				boolean snow = biome.startsWith("ice") || biome.startsWith("frozen") || biome.startsWith("tundra") || biome.startsWith("taiga");
				gui.updateSnow(snow);
				gui.updateGamemode(sp.getGameMode().equals(GameMode.CREATIVE));
			}
		}
	}
}
