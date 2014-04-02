package org.dyndns.pamelloes.EIA;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class EnoughItemsAlready extends JavaPlugin {
	private static Logger log = Logger.getLogger("minecraft");
	
	public static Map<SpoutPlayer, EIAGui> map = new HashMap<SpoutPlayer, EIAGui>();
	
	private static EnoughItemsAlready eia;
	private static EIAListener eial;
	
	public static EnoughItemsAlready getInstance() {
		return eia;
	}
	
	public static EIAListener getListener() {
		return eial;
	}
	
	public void onEnable() {
		eia = this;
		eial = new EIAListener(this);
		
		EnoughItemsAlready.extractFile("CreativeButtonOff.png", true);
		EnoughItemsAlready.extractFile("CreativeButtonOn.png", true);
		EnoughItemsAlready.extractFile("DawnButtonOff.png", true);
		EnoughItemsAlready.extractFile("DawnButtonOn.png", true);
		EnoughItemsAlready.extractFile("DuskButtonOff.png", true);
		EnoughItemsAlready.extractFile("DuskButtonOn.png", true);
		EnoughItemsAlready.extractFile("HealButtonOff.png", true);
		EnoughItemsAlready.extractFile("HealButtonOn.png", true);
		EnoughItemsAlready.extractFile("MidnightButtonOff.png", true);
		EnoughItemsAlready.extractFile("MidnightButtonOn.png", true);
		EnoughItemsAlready.extractFile("NoonButtonOff.png", true);
		EnoughItemsAlready.extractFile("NoonButtonOn.png", true);
		EnoughItemsAlready.extractFile("OpenButtonOff.png", true);
		EnoughItemsAlready.extractFile("OpenButtonOn.png", true);
		EnoughItemsAlready.extractFile("RainButtonOff.png", true);
		EnoughItemsAlready.extractFile("RainButtonOn.png", true);
		EnoughItemsAlready.extractFile("Recipe.png", true);
		EnoughItemsAlready.extractFile("RecipeButtonOff.png", true);
		EnoughItemsAlready.extractFile("RecipeButtonOn.png", true);
		EnoughItemsAlready.extractFile("SaveButtonOff.png", true);
		EnoughItemsAlready.extractFile("SaveButtonOn.png", true);
		EnoughItemsAlready.extractFile("SnowButtonOff.png", true);
		EnoughItemsAlready.extractFile("SnowButtonOn.png", true);
		EnoughItemsAlready.extractFile("TrashButtonOff.png", true);
		EnoughItemsAlready.extractFile("TrashButtonOn.png", true);
		
		log("Enabled.");
	}
	
	public void onDisable() {
		log("Disabled.");
	}
	
	public static void log(String message) {
		log.info("[EnoughItemsAlready] " + message);
	}
	
	public static void severe(String message) {
		log.severe("[EnoughItemsAlready] " + message);
	}
	
	/**
	 * Extract files from the plugin jar and optionally cache them on the client.
	 * @param regex a pattern of files to extract
	 * @param cache if any files found should be added to the Spout cache
	 * @return if any files were extracted
	 */
	public static boolean extractFile(String regex, boolean cache) {
		boolean found = false;
		try {
			JarFile jar = new JarFile(getInstance().getFile());
			for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
				JarEntry entry = (JarEntry) entries.nextElement();
				String name = entry.getName();
				if (name.matches(regex)) {
					if (!getInstance().getDataFolder().exists()) {
						getInstance().getDataFolder().mkdir();
					}
					try {
						File file = new File(getInstance().getDataFolder(), name);
						if (!file.exists()) {
							InputStream is = jar.getInputStream(entry);
							FileOutputStream fos = new FileOutputStream(file);
							while (is.available() > 0) {
								fos.write(is.read());
							}
							fos.close();
							is.close();
							found = true;
						}
						if (cache && name.matches(".*\\.(txt|yml|xml|png|jpg|ogg|midi|wav|zip)$")) {
							SpoutManager.getFileManager().addToPreLoginCache(getInstance(), file);
						}
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
		}
		return found;
	}
}
