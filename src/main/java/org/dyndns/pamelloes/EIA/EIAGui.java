package org.dyndns.pamelloes.EIA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.ComboBox;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericComboBox;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.gui.TextField;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.player.SpoutPlayer;

public class EIAGui {
	private static final List<Material> materials;
	
	private List<Material> active = null;
	
	private final SpoutPlayer player;
	
	private final EIASlot[] slots;
	private int page = 0;
	private int pagecount;
	private final Gradient itembg, buttonbg, invbg;
	private final Button next, previous, delete;
	private final ImageCheckBox save, load;
	private final TextField search;
	private final GenericLabel recipeLabel;
	private String searchstr = "";
	private final ImageCheckBox[] buttons;
	private final ComboBox saves;
	private boolean snow = false;
	private boolean recipe = true;
	
	public EIAGui(SpoutPlayer player) {
		this.player = player;
		
		/////////
		//ITEMS//
		/////////
		int width = 110;
		
		next = new GenericButton("Next") {
			@Override
			public void onButtonClick(ButtonClickEvent e) {
				nextPage();
			}
		};
		next.setWidth(width/2).setHeight(20).setX(-width/2).setY(0).setMargin(0).setAnchor(WidgetAnchor.TOP_RIGHT);
		
		previous = new GenericButton("Back") {
			@Override
			public void onButtonClick(ButtonClickEvent e) {
				previousPage();
			}
		};
		previous.setWidth(width/2).setHeight(20).setX(-width).setY(0).setMargin(0).setAnchor(WidgetAnchor.TOP_RIGHT);
		
		itembg = new GenericGradient(new Color(0f,0f,0f));
		itembg.setWidth(width).setHeight(200).setMargin(0).setPriority(RenderPriority.Highest).setAnchor(WidgetAnchor.TOP_RIGHT).setX(-width).setY(next.getHeight());

		slots = new EIASlot[11 * 6];
		for(int i = 0; i < 11; i++) {
			for(int j = 0; j < 6; j++) {
				EIASlot s = new EIASlot(MaterialData.air);
				s.setRenderAmount(false);
				s.setWidth(16).setHeight(16).setFixed(true).setAnchor(WidgetAnchor.TOP_RIGHT).setX((-itembg.getWidth()) + (j * 18) + 2).setY(next.getHeight() + (i * 18) + 2);
				slots[(i * 6) + j] = s;
			}
		}
		updateItems(null);
		
		search = new GenericTextField();
		search.setPlaceholder("Search (SLOW!!!)").setMaximumCharacters(100).setAnchor(WidgetAnchor.TOP_RIGHT).setWidth(width - 4).setHeight(13).setX(-width + 2).setY(itembg.getY() + itembg.getHeight() + 2).setMargin(0);
		
		recipeLabel = new GenericLabel("");
		if (recipe) {
			recipeLabel.setText("Click Item to View Recipe");
		}
		recipeLabel.setAnchor(WidgetAnchor.TOP_RIGHT).setWidth(width - 4).setHeight(13).setX(-width + 2).setY(itembg.getY() + itembg.getHeight() + 20).setMargin(0);
		
		
		////////////
		//CONTROLS//
		////////////
		
		int margin = 2;
		int size = 20;
		
		buttons = new ImageCheckBox[9];
		buttons[0] = new ImageCheckBox(getBase() + "TrashButtonOff.png", getBase() + "TrashButtonOn.png", "eia.display.button.delete"); //trash
		buttons[0].setWidth(size).setHeight(size).setMargin(0).setAnchor(WidgetAnchor.TOP_LEFT).setTooltip("Activate Delete Mode\nThe items you click on will be deleted.");
		
		buttons[1] = new ImageCheckBox(getBase() + "CreativeButtonOff.png", getBase() + "CreativeButtonOn.png", "eia.display.button.gamemode") { //creative
			@Override
			public void onButtonClick(ButtonClickEvent e) {
				EIAGui.this.player.setGameMode(isChecked() ? GameMode.CREATIVE : GameMode.SURVIVAL);
			}
		};
		buttons[1].setWidth(size).setHeight(size).setMargin(0).setAnchor(WidgetAnchor.TOP_LEFT).setTooltip("Change Gamemode\nOn means you are in Creative.");
		
		buttons[2] = new ImageCheckBox(getBase() + "RainButtonOff.png", getBase() + "RainButtonOn.png", "eia.display.button.weather") { //rain
			@Override
			public void onButtonClick(ButtonClickEvent e) {
				EIAGui.this.player.getWorld().setStorm(isChecked());
			}
		};
		buttons[2].setWidth(size).setHeight(size).setMargin(0).setAnchor(WidgetAnchor.TOP_LEFT).setTooltip("Change the Weather\nOn means it is rainging or snowing.");
		
		buttons[3] = new ImageButton(getBase() + "HealButtonOff.png", "eia.display.button.heal") { //heal
			@Override
			public void onButtonClick(ButtonClickEvent e) {
				EIAGui.this.player.setHealth(20.0);
			}
		};
		buttons[3].setWidth(size).setHeight(size).setMargin(0).setAnchor(WidgetAnchor.TOP_LEFT).setTooltip("Heal youself fully.");
		
		buttons[4] = new ImageCheckBox(getBase() + "RecipeButtonOff.png", getBase() + "RecipeButtonOn.png", "eia.display.button.recipe") { //rain
			@Override
			public void onButtonClick(ButtonClickEvent e) {
				recipe = isChecked();
			}
		};
		buttons[4].setWidth(size).setHeight(size).setMargin(0).setAnchor(WidgetAnchor.TOP_LEFT).setTooltip("Activate Recipe Mode\nWhen you click on an item, you will see its recipe(s).");
		buttons[4].setChecked(recipe);
		
		buttons[5] = new ImageCheckBox(getBase() + "DawnButtonOff.png", getBase() + "DawnButtonOn.png", "eia.display.button.time") { //dawn
			@Override
			public void onButtonClick(ButtonClickEvent e) {
				EIAGui.this.player.getWorld().setTime(0);
				updateTime(0);
			}
		};
		buttons[5].setWidth(size).setHeight(size).setMargin(0).setAnchor(WidgetAnchor.TOP_LEFT).setTooltip("Sets the time to dawn.");
		
		buttons[6] = new ImageCheckBox(getBase() + "NoonButtonOff.png", getBase() + "NoonButtonOn.png", "eia.display.button.time") { //noon
			@Override
			public void onButtonClick(ButtonClickEvent e) {
				EIAGui.this.player.getWorld().setTime(6000);
				updateTime(1);
			}
		};
		buttons[6].setWidth(size).setHeight(size).setMargin(0).setAnchor(WidgetAnchor.TOP_LEFT).setTooltip("Sets the time to noon.");
		
		buttons[7] = new ImageCheckBox(getBase() + "DuskButtonOff.png", getBase() + "DuskButtonOn.png", "eia.display.button.time") { //dusk
			@Override
			public void onButtonClick(ButtonClickEvent e) {
				EIAGui.this.player.getWorld().setTime(12000);
				updateTime(2);
			}
		};
		buttons[7].setWidth(size).setHeight(size).setMargin(0).setAnchor(WidgetAnchor.TOP_LEFT).setTooltip("Sets the time to dusk.");
		
		buttons[8] = new ImageCheckBox(getBase() + "MidnightButtonOff.png", getBase() + "MidnightButtonOn.png", "eia.display.button.time") { //midnight
			@Override
			public void onButtonClick(ButtonClickEvent e) {
				EIAGui.this.player.getWorld().setTime(18000);
				updateTime(3);
			}
		};
		buttons[8].setWidth(size).setHeight(size).setMargin(0).setAnchor(WidgetAnchor.TOP_LEFT).setTooltip("Sets the time to midnight.");
		
		int x = margin, y = margin;
		int xcount = 0, ycount = 0;
		int maxX = 5;
		Iterator<ImageCheckBox> iterator = Arrays.asList(buttons).iterator();
		while(iterator.hasNext()) {
			for(xcount = 0, x = margin; xcount < maxX && iterator.hasNext(); xcount++) {
				ImageCheckBox ic = iterator.next();
				if(!player.hasPermission(ic.getPermission())) {
					xcount--;
					ic.setX(-100).setY(-100); //safely off screen
					continue;
				}
				ic.setX(x).setY(y);
				x += size + margin;
			}
			y += size + margin;
			ycount++;
		}
		
		buttonbg = new GenericGradient(new Color(0f,0f,0f));
		if (xcount == 0 && ycount == 1) buttonbg.setX(-100).setY(-100);
		else buttonbg.setWidth(((ycount==1 ? xcount : maxX) * (size + margin)) + margin).setHeight(y).setMargin(0).setPriority(RenderPriority.Normal).setAnchor(WidgetAnchor.TOP_LEFT).setX(0).setY(0);

		saves = new GenericComboBox();
		save = new ImageCheckBox(getBase() + "SaveButtonOff.png", getBase() + "SaveButtonOn.png") {
			@Override
			public void onButtonClick(ButtonClickEvent e) {
				if(InventorySave.getInstance().getInventories(EIAGui.this.player).length >= 10) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
						public void run() {
							setChecked(false);
						}
					}, 1l);
					return;
				}
				try {
					for(int i = 1; !InventorySave.getInstance().saveInventory(EIAGui.this.player, "Save " + i, EIAGui.this.player.getInventory(), false); i++);
				} catch (Exception ex) {
					ex.printStackTrace(); //didn't work.
				}
				saves.setItems(Arrays.asList(InventorySave.getInstance().getInventories(EIAGui.this.player))).setDirty(true);
				Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
					public void run() {
						setChecked(false);
					}
				}, 1l);
			}
		};
		load = new ImageCheckBox(getBase() + "OpenButtonOff.png", getBase() + "OpenButtonOn.png") {
			@Override
			public void onButtonClick(ButtonClickEvent e) {
				try {
					EIAGui.this.player.getInventory().setContents(InventorySave.getInstance().loadInventory(EIAGui.this.player, saves.getSelectedItem()).getContents());
				} catch (Exception ex) {
					//didn't work.
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
					public void run() {
						setChecked(false);
					}
				}, 1l);
			}
		};
		delete = new GenericButton("Delete") {
			private int stage = 0;
			private long change = -1;

			@Override
			public void onButtonClick(ButtonClickEvent e) {
				if(stage == 0) {
					setText("Really?").setColor(new Color(255,10,10));
					stage = 1;
					change = System.currentTimeMillis();
				} else {
					InventorySave.getInstance().deleteInventory(EIAGui.this.player, saves.getSelectedItem() != null ? saves.getSelectedItem() : "");
					setText("Delete").setColor(new Color(1.0f,1.0f,1.0f));
					saves.setItems(Arrays.asList(InventorySave.getInstance().getInventories(EIAGui.this.player))).setDirty(true);
					stage = 0;
					change = -1;
				}
			}
			
			@Override
			public void onTick() {
				super.onTick();
				if(change < 0 || System.currentTimeMillis() - change <= 5000) return;
				setText("Delete").setColor(new Color(1.0f,1.0f,1.0f));
				stage = 0;
				change = -1;
			}
		};
		
		invbg = new GenericGradient(new Color(0f,0f,0f));
		if(player.hasPermission("eia.display.saveinventory")) {
			saves.setFormat("%selected%").setWidth(68).setHeight(20).setX(-73 - (int) (1.5 * margin)).setY(margin).setAnchor(WidgetAnchor.TOP_CENTER).setMargin(0).setPriority(RenderPriority.Low);
			saves.setItems(Arrays.asList(InventorySave.getInstance().getInventories(player))).setTooltip("Sets the active inventory save for loading or deleting.");
			save.setWidth(20).setHeight(20).setX(-5 + (int) (-0.5 * margin)).setY(margin).setAnchor(WidgetAnchor.TOP_CENTER).setMargin(0).setTooltip("Saves your inventory to the first available save.\nYou have ten saves.");
			load.setWidth(20).setHeight(20).setX(15 + (int) (0.5 * margin)).setY(margin).setAnchor(WidgetAnchor.TOP_CENTER).setMargin(0).setTooltip("Loads the inventory saved to the selected save.");
			delete.setWidth(38).setHeight(20).setX(35 + (int) (1.5 * margin)).setY(margin).setAnchor(WidgetAnchor.TOP_CENTER).setMargin(0).setPriority(RenderPriority.Low).setTooltip("Deletes the selected inventory save");
			invbg.setWidth(146 + margin + margin).setHeight(margin + 20 + margin).setX(-(invbg.getWidth()/2)).setY(0).setAnchor(WidgetAnchor.TOP_CENTER).setMargin(0).setPriority(RenderPriority.Normal);
		} else {
			saves.setX(-100).setY(-100);
			save.setX(-100).setY(-100);
			load.setX(-100).setY(-100);
			delete.setX(-100).setY(-100);
			invbg.setX(-100).setY(-100);
		}
	}
	
	private void nextPage() {
		page++;
		page %= pagecount;
		updateItems();
	}
	
	private void previousPage() {
		page--;
		if(page < 0) page = pagecount - 1;
		updateItems();
	}
	
	private void updateItems() {
		int start = slots.length * page;
		Iterator<Material> iter = active.listIterator(start);
		for(EIASlot s : slots) {
			s.setMaterial(iter.hasNext() ? iter.next() : MaterialData.air);
			
		}
	}
	
	private void updateItems(String search) {
		if(searchstr == search) return; //both null;
		if(searchstr != null && searchstr.equals(search)) return;
		searchstr = search;
		boolean reset = false;
		if(search == null) reset = true;
		else if(search.trim().equals("")) reset = true;
		if(active == null) active = new ArrayList<Material>();
		else active.clear();
		for(Material m : materials) {
			if(player.hasPermission("eia.display.item.*") || player.hasPermission("eia.display.item." + m.getName().toLowerCase().replace(" ", "")) || player.hasPermission("eia.display.item." + m.getRawId()) || player.hasPermission("eia.display.item." + m.getRawId() + "-" + m.getRawData())) {
				if(reset || m.getName().toLowerCase().contains(search.toLowerCase())) {					
					active.add(m);
				}
			}
		}
		page = 0;
		pagecount = ((active.size() - (active.size() % (slots.length))) / (slots.length)) + (active.size() % (slots.length) != 0 ? 1 : 0);
		if(pagecount == 0) pagecount = 1;
		
		updateItems();
	}
	
	private String getBase() {
		return "plugins/" + EnoughItemsAlready.getInstance().getDescription().getName() + "/";
	}
	
	public void attach(Screen screen) {
		screen.attachWidgets(EnoughItemsAlready.getInstance(), buttonbg, itembg, invbg, next, previous, search, recipeLabel, saves, save, load, delete);
		screen.attachWidgets(EnoughItemsAlready.getInstance(), slots);
		updateGamemode(player.getGameMode().equals(GameMode.CREATIVE));
		updateWeather(player.getWorld().hasStorm());
		String biome = player.getWorld().getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).toString().toLowerCase();
		boolean snow = biome.startsWith("ice") || biome.startsWith("frozen") || biome.startsWith("tundra") || biome.startsWith("taiga");
		updateSnow(snow);
		int index = (int) (player.getWorld().getTime()) % 24000;
		index = (index - (index % 6000)) / 6000;
		updateTime(index);
		screen.attachWidgets(EnoughItemsAlready.getInstance(), buttons);
	}

	public boolean hasClearMode() {
		return buttons[0].isChecked();
	}
	
	public boolean hasRecipeMode() {
		return recipe;
	}
	
	public void updateTime(int index) {
		for(int i = 5; i < 9; i++) buttons[i].setChecked(i == index + 5);
	}
	
	public void updateSnow(boolean snowing) {
		if(snow == snowing) return;
		snow = snowing;
		buttons[2].setImages(getBase() + (snowing ? "Snow" : "Rain") + "ButtonOff.png", getBase() + (snowing ? "Snow" : "Rain") + "ButtonOn.png");
	}
	
	public void updateWeather(boolean raining) {
		buttons[2].setChecked(raining);
	}
	
	public void updateGamemode(boolean creative) {
		buttons[1].setChecked(creative);
	}
	
	public void updateSearch() {
		updateItems(search.getText());
	}
	
	static {
		materials = new ArrayList<Material>();
		for(Material mat : MaterialData.getMaterials()) {
			if(!(mat instanceof CustomBlock)) {
				materials.add(mat);
			}
		}
	}
}
