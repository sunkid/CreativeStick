/**
 * LICENSING
 * 
 * This software is copyright by sunkid <sunkid@iminurnetz.com> and is
 * distributed under a dual license:
 * 
 * Non-Commercial Use:
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Commercial Use:
 *    Please contact sunkid@iminurnetz.com
 */

package com.iminurnetz.bukkit.plugin.creativestick;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.iminurnetz.bukkit.plugin.BukkitPlugin;
import com.iminurnetz.bukkit.util.MaterialUtils;
import com.iminurnetz.util.PersistentProperty;

public class ConfigurationService {

	private Configuration config;
	private File configFile;
	private BukkitPlugin plugin;
	
	// used to check the config file for updates
    public static final String LAST_CHANGED_IN_VERSION = "0.7.1";
	
	// TODO convert to enums
	// global settings
	public static final String SETTINGS_TAG = "settings";
	
	public static final boolean ALLOW_OPS = true;
	public static final boolean IS_ENABLED = false;
	public static final boolean DOES_DROP = true;
	
	// user settings
	public static final String USER_SETTINGS_TAG = SETTINGS_TAG + ".user";
	
	public static final int UNDO_AMOUNT = 5;
    public static final int MAX_UNDOS = 500;
	public static final int DISTANCE = 100;
	public static final Material TOOL = Material.STICK;
	public static final boolean PROTECT_BOTTOM = true;
    public static final boolean RIGHT_CLICK_SWITCH = false;
    public static final boolean RIGHT_CLICK_MODES = false;
	public static final boolean DEBUG = false;
	public static final boolean NATURAL_DROPS = false;
	public static final boolean ANNOUNCE = false;
    public static final boolean THROW_BUILD = false;
	
	// permissions
	public static final String PERMISSIONS_TAG = "permissions";
	
	public ConfigurationService(BukkitPlugin plugin) {
		this.configFile = new File(plugin.getDataFolder(), "config.yml");		
		this.config = plugin.getConfiguration();
		this.plugin = plugin;
		load();
	}
	
	private void load() {
		File dir = configFile.getParentFile();
		
		// we are relying on a value that is always set
		ConfigurationNode settings = config.getNode(SETTINGS_TAG);
		boolean oldConfigFound = false;
		
		if (settings == null) {				
			// check if we are migrating from an old installation of iStick
			File oldDir = new File(dir.getParentFile().getParentFile(), "iStick");
			
			if (!oldDir.exists()) {
				oldDir = new File(dir.getParentFile(), "iStick");
			}
			
			if (oldDir.exists()) {
				plugin.log("Migrating " + oldDir.getAbsolutePath() + " data directory");		
				if (oldDir.renameTo(dir))
					plugin.log("copied to " + dir.getAbsolutePath());
				oldConfigFound = true;
			} else if (!dir.exists()){
				// plugin.log(oldDir.getPath() + " not present");
				dir.mkdirs();
			}			
		}
		
		File oldCfg = new File(dir, "iStick.permissions");
		oldConfigFound = oldCfg.exists();
		
		// load the old configuration file (if any)
		if (settings == null || (settings.getBoolean("ignore-yml", false) && oldConfigFound)) {
			if (settings != null && settings.getBoolean("ignore-yml", false))
				config.setProperty(PERMISSIONS_TAG, null);
			oldConfigFound = loadOldConfig(dir);
		}
		
		if (settings == null) {
			generateDefaultConfig(dir);
		} else // check if there was an update to the config file/parameters
		if (!config.getString(SETTINGS_TAG + ".version", "").equals(LAST_CHANGED_IN_VERSION)) {
			plugin.log(Level.WARNING, "Your configuration file is outdated, please read config-new.yml");
			URL shipped = getClass().getResource("/config.yml");
			byte[] buf = new byte[1024];
			int len;
			try {
				InputStream in = shipped.openStream();
				OutputStream out = new FileOutputStream(new File(dir, "config-new.yml"));
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			} catch (IOException e) {
				plugin.getLogger().getLogger().log(Level.SEVERE, "Cannot generate config-new.file file", e);
			}
		}
		
		// reload the configuration unless the old one is overriding
		if (settings == null || !oldConfigFound) {
			config.load();
			config.setProperty(SETTINGS_TAG + ".ignore-yml", new Boolean(false));
		} 
		

		if (config.getBoolean(SETTINGS_TAG + ".ignore-yml", false)) {
			plugin.log(Level.WARNING, "ignoring new configuration file; please consider migrating");
		}
	}
	
	public CSPermissionHandler getPermissionHandler() {
		CSPermissionHandler h = new CSPermissionHandler(plugin, config.getBoolean(SETTINGS_TAG + ".allow-ops", ALLOW_OPS));
		
		if (h.usesOwnPermissions()) {
			List<String> users = config.getKeys(PERMISSIONS_TAG);
			if (users != null) {
				for (String user : users) {
					plugin.log("setting permissions for " + user);
					List<String> confPerms = config.getStringList(PERMISSIONS_TAG + "." + user, new ArrayList<String>());				
					for (String p : confPerms)
						h.addPermission(user, p);
				}
			}
			
		}
		
		return h;
	}

	private void generateDefaultConfig(File dir) {
		// use the default configuration file shipped with the jar
		URL defCon = getClass().getResource("/config.yml");
		byte[] buf = new byte[1024];
		int len;
		try {
			InputStream in = defCon.openStream();
			OutputStream out = new FileOutputStream(configFile);
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();

			File tmpFile = new File(dir, "tmp.yml");
			Configuration tmp = new Configuration(tmpFile);
			tmp.setProperty(PERMISSIONS_TAG, config.getProperty(PERMISSIONS_TAG));
			tmp.save();
			
			in = new FileInputStream(tmpFile);
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			
			tmpFile.delete();
		} catch (IOException e) {
			plugin.getLogger().getLogger().log(Level.SEVERE, "Cannot generate config file", e);
		}	
	}

	private boolean loadOldConfig(File dir) {

		boolean oldConfigFound = false;
		
		// try to load the old settings
		PersistentProperty pprop = null;
		
		try {
			pprop = new PersistentProperty(dir, "iStick.permissions", false);
			oldConfigFound = true;
			plugin.log("Loading old permission file.");
		} catch (IOException e) {
			// ignored!
		}

		if (oldConfigFound) {
			String users = pprop.getString("can-use");
	
			if (users != null) {
				String[] allUsers = users.split(",\\s*");
				for (String u : allUsers) {
					ArrayList<String> p = new ArrayList<String>();
					p.add(CSPermissionHandler.CAN_USE_PERMISSION);
					config.setProperty(PERMISSIONS_TAG + "." + u, p);
					plugin.log("set permission for " + u + " to " + config.getList(PERMISSIONS_TAG + "." + u).get(0));
				}
			}
		}
		
		return oldConfigFound;
	}

	public boolean isEnabled() {
		return config.getBoolean(SETTINGS_TAG + ".enabled", IS_ENABLED);
	}

	public boolean doesDrop() {
		return config.getBoolean(SETTINGS_TAG + ".drops", DOES_DROP);
	}

	public int getUndoAmount() {
		return config.getInt(USER_SETTINGS_TAG + ".undo", UNDO_AMOUNT);
	}
	
	public int getDistance() {
		return config.getInt(USER_SETTINGS_TAG + ".distance", DISTANCE);
	}

	public Material getTool() {
		return MaterialUtils.getMaterial(config.getString(USER_SETTINGS_TAG + ".tool", TOOL.name()));
	}
	
	public boolean doProtectBottom() {
		return config.getBoolean(USER_SETTINGS_TAG + ".protect-bottom", PROTECT_BOTTOM);
	}

	public boolean doRightClickSwitch() {
		return config.getBoolean(USER_SETTINGS_TAG + ".right-click-switch", RIGHT_CLICK_SWITCH);
	}

    public boolean doRightClickModes() {
        return config.getBoolean(USER_SETTINGS_TAG + ".right-click-modes", RIGHT_CLICK_MODES);
    }

	public boolean isDebug() {
		return config.getBoolean(USER_SETTINGS_TAG + ".debug-mode", DEBUG);
	}

	public boolean doesNaturalDrops() {
		return config.getBoolean(USER_SETTINGS_TAG + ".natural-drops", NATURAL_DROPS);
	}
	
	public boolean doAnnounce() {
		return config.getBoolean(USER_SETTINGS_TAG + ".announce", ANNOUNCE);
	}

	public HashSet<Byte> getIgnored() {
		List<String> names = config.getStringList(SETTINGS_TAG + ".ignore", null);
		HashSet<Material> materials = new HashSet<Material>();
		for (String name : names) {
			Material m = MaterialUtils.getMaterial(name);
			if (m != null)
				materials.add(m);
		}
		
		HashSet<Byte> returnSet = new HashSet<Byte>();
		for (Material m : materials) {
			returnSet.add((byte) m.getId());
		}
		returnSet.add((byte) Material.AIR.getId());
		return returnSet;
	}

    public int getMaxUndos() {
        return config.getInt(USER_SETTINGS_TAG + ".max-undos", MAX_UNDOS);
    }

    public boolean useBlockSpawnPermission() {
        return config.getBoolean(SETTINGS_TAG + ".use-block-spawn-permission", false);
    }

    public boolean isThrowBuild() {
        return config.getBoolean(USER_SETTINGS_TAG + ".throw-build", THROW_BUILD);
    }
}
