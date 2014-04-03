package org.dyndns.pamelloes.EIA;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.ItemWidget;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.Texture;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.player.SpoutPlayer;

public class EIARecipeGui {

	@SuppressWarnings("deprecation")
	public static void showRecipes(ItemStack i, final SpoutPlayer p) {
		final ScreenType st = p.getCurrentScreen().getScreenType();		
		Material m = MaterialData.getMaterial(i.getTypeId(), i.getDurability());
		if(m == null) return;
		
		List<Recipe> recipelist = new ArrayList<Recipe>();
		Iterator<Recipe> iter = Bukkit.recipeIterator();
		recipes: while(iter.hasNext()) {
			Recipe r = iter.next();
			if(r instanceof FurnaceRecipe) continue;
			if(r instanceof ShapelessRecipe) {
				for(ItemStack is : ((ShapelessRecipe) r).getIngredientList()) {
					if(is != null && m.equals(MaterialData.getMaterial(is.getTypeId(), is.getDurability()))) {
						recipelist.add(r);
						continue recipes;
					}
				}
			}
			if(r instanceof ShapedRecipe) {
				for(ItemStack is : ((ShapedRecipe) r).getIngredientMap().values()) {
					if(is != null && m.equals(MaterialData.getMaterial(is.getTypeId(), is.getDurability()))) {
						recipelist.add(r);
						continue recipes;
					}
				}
			}
			if(m.equals(MaterialData.getMaterial(r.getResult().getTypeId(), r.getResult().getDurability()))) recipelist.add(r);
		}
		
		final Recipe[] recipes = recipelist.toArray(new Recipe[0]);
		if(recipes.length == 0) return;
		
		
		PopupScreen popup = new GenericPopup() {
			
			@Override
			public void onScreenClose(final ScreenCloseEvent e) {
				if(!e.isCancelled()) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
						public void run() {
							if (st.equals(ScreenType.PLAYER_INVENTORY_CREATIVE) || st.equals(ScreenType.PLAYER_INVENTORY)) {
								p.openScreen(st);
							} else {
								p.closeActiveWindow(); //Close inventory window if its open.
							}
						}
					}, 1l);
				}
			}
		};
		p.getMainScreen().attachPopupScreen(popup);
		Texture t = showGui(recipes[0], p);
		Button next = new MainButton("Next", recipes);
		next.setWidth(t.getWidth()/2).setHeight(20).setAnchor(WidgetAnchor.CENTER_CENTER).setX(t.getX() + (t.getWidth()/2)).setY(t.getY() - 20);
		Button previous = new CompanionButton("Back", (MainButton) next);
		previous.setWidth(t.getWidth()/2).setHeight(20).setAnchor(WidgetAnchor.CENTER_CENTER).setX(t.getX()).setY(t.getY() - 20);
		popup.attachWidgets(EnoughItemsAlready.getInstance(), next, previous);
	}
	
	
	public static Texture showGui(Recipe r, SpoutPlayer p) {
		if (r instanceof ShapedRecipe) return showMatrixRecipe(getMatrix((ShapedRecipe) r), r.getResult(), p);
		else if (r instanceof ShapelessRecipe) return showMatrixRecipe(getMatrix((ShapelessRecipe) r), r.getResult(), p);
		else if (r instanceof FurnaceRecipe) showFurnaceRecipe((FurnaceRecipe) r, p);
		return null;
	}
	
	@SuppressWarnings("deprecation")
	private static ItemStack[][] getMatrix(ShapedRecipe r) {
		//cuz bukkit fails, I have to do some nasty refactoring here....
		List<ItemStack> itemlist = new ArrayList<ItemStack>(0);
		int width, height;
		width = r.getShape().length;
		height = r.getShape()[0].length();
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				itemlist.add(r.getIngredientMap().get(r.getShape()[i].charAt(j)));
			}
		}
		Iterator<ItemStack> iter = itemlist.iterator();
		
		ItemStack[][] items = new ItemStack[3][3];
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(width <= i) items[i][j] = new ItemStack(0);
				else if(height <= j) items[i][j] = new ItemStack(0);
				else if(!iter.hasNext()) items[i][j] = new ItemStack(0);
				else items[i][j] = iter.next();
			}
		}
		return items;
	}
	
	private static ItemStack[][] getMatrix(ShapelessRecipe r) {
		ItemStack[][] items = new ItemStack[3][3];
		Iterator<ItemStack> iter = r.getIngredientList().iterator();
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(iter.hasNext()) items[i][j] = iter.next();
				else items[i][j] = new ItemStack(0);
			}
		}
		return items;
	}
	
	@SuppressWarnings("deprecation")
	private static Texture showMatrixRecipe(ItemStack[][] matrix, ItemStack result, SpoutPlayer p) {
		Texture bg = new GenericTexture("plugins/" + EnoughItemsAlready.getInstance().getDescription().getName() + "/Recipe.png");
		bg.setWidth(250).setHeight(144).setX(-bg.getWidth()/2).setY(-bg.getHeight()/2).setAnchor(WidgetAnchor.CENTER_CENTER).setPriority(RenderPriority.Normal);
		List<ItemWidget> items = new ArrayList<ItemWidget>();
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(matrix[i][j] == null || matrix[i][j].getTypeId() == 0 || matrix[i][j].getAmount() == 0) continue;
				ItemWidget iw = new GenericItemWidget(matrix[i][j]);
				iw.setFixed(true).setWidth(32).setHeight(32).setAnchor(WidgetAnchor.CENTER_CENTER).setX((-bg.getWidth()/2) + 12 + (j * 36)).setY((-bg.getHeight()/2) + 30 + (i * 36)).setPriority(RenderPriority.Lowest);				
				Material item = MaterialData.getMaterial(matrix[i][j].getTypeId(), matrix[i][j].getDurability());
				String custom = item != null ? String.format(item.getName(), String.valueOf(matrix[i][j].getDurability())) : null;
				if (item == null) {
					System.out.println("Result ID: "+ result.getTypeId());
					System.out.println("Result Durability: "+ result.getDurability());
				}
				if (custom != null) {
					iw.setTooltip(custom); 
				} else if (item != null) {
					iw.setTooltip(item.getName());
				}
				items.add(iw);
			}
		}
		ItemWidget iw = new GenericItemWidget(result);
		iw.setFixed(true).setWidth(32).setHeight(32).setAnchor(WidgetAnchor.CENTER_CENTER).setX((-bg.getWidth()/2) + 192 + 8).setY((-bg.getHeight()/2) + 58 + 8).setPriority(RenderPriority.Lowest);
		Material item = MaterialData.getMaterial(result.getTypeId(), result.getDurability());
		String custom = item != null ? String.format(item.getName(), String.valueOf(result.getDurability())) : null;
		if (item == null) {
			System.out.println("Result ID: "+ result.getTypeId());
			System.out.println("Result Durability: "+ result.getDurability());
		}
		if (custom != null) {
			iw.setTooltip(custom); 
		} else if (item != null) {
			iw.setTooltip(item.getName());
		}		
		items.add(iw);
		p.getMainScreen().getActivePopup().attachWidgets(EnoughItemsAlready.getInstance(), bg);
		p.getMainScreen().getActivePopup().attachWidgets(EnoughItemsAlready.getInstance(), items.toArray(new Widget[0]));
		return bg;
	}
	
	private static void showFurnaceRecipe(FurnaceRecipe r, SpoutPlayer p) {
		
	}
	
	private static class MainButton extends GenericButton {
		private final Recipe[] recipes;
		private int index = 0;
		
		private CompanionButton companion;
		
		public MainButton(String name, Recipe[] recipes) {
			super(name);
			this.recipes = recipes;
		}
		
		public void setCompanion(CompanionButton comp) {
			companion = comp;
		}
		
		@Override
		public void onButtonClick(ButtonClickEvent e) {
			changeIndex(1);
		}
		
		public void changeIndex(int offset) {
			index += offset;
			index %= recipes.length;
			if(index < 0) index += recipes.length;
			
			Screen s = getScreen();
			s.removeWidgets(getPlugin());
			s.attachWidgets(getPlugin(), this, companion);
			
			showGui(recipes[index], s.getPlayer());
		}
	}
	
	private static class CompanionButton extends GenericButton {
		private final MainButton main;
		
		public CompanionButton(String name, MainButton main) {
			super(name);
			this.main = main;
			main.setCompanion(this);
		}
		
		@Override
		public void onButtonClick(ButtonClickEvent e) {
			main.changeIndex(-1);
		}
	}
}
