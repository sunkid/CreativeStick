package com.iminurnetz.bukkit.creativestick;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.iminurnetz.bukkit.util.MaterialUtils;
import com.iminurnetz.util.PersistentProperty;
import com.nijikokun.bukkit.istick.IStick;

public class ConfigurationService {

	private Configuration config;
	private File configFile;
	
	// used to check the config file for updates
	public static final String LAST_CHANGED_IN_VERSION = "0.4.15";
	
	// TODO convert to enums
	// global settings
	public static final String SETTINGS_TAG = "settings";
	
	public static final boolean ALLOW_OPS = true;
	public static final boolean IS_ENABLED = false;
	public static final boolean DOES_DROP = true;
	
	// user settings
	public static final String USER_SETTINGS_TAG = SETTINGS_TAG + ".user";
	
	public static final int UNDO_AMOUNT = 5;
	public static final int DISTANCE = 100;
	public static final Material TOOL = Material.STICK;
	public static final boolean PROTECT_BOTTOM = true;
	public static final boolean DEBUG = false;
	
	// permissions
	public static final String PERMISSIONS_TAG = "permissions";
	
	public ConfigurationService(File dataFolder, Configuration config) {
		this.configFile = new File(dataFolder, "config.yml");		
		this.config = config;
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
				IStick.log(Level.INFO, "Migrating " + oldDir.getAbsolutePath() + " data directory");		
				if (oldDir.renameTo(dir))
					IStick.log(Level.INFO, "copied to " + dir.getAbsolutePath());
				oldConfigFound = true;
			} else if (!dir.exists()){
				// IStick.log(Level.INFO, oldDir.getPath() + " not present");
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
			IStick.log(Level.WARNING, "Your configuration file is outdated, please read config-new.yml");
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
				IStick.getLogger().log(Level.SEVERE, "Cannot generate config-new.file file", e);
			}
		}
		
		// reload the configuration unless the old one is overriding
		if (settings == null || !oldConfigFound) {
			config.load();
			config.setProperty(SETTINGS_TAG + ".ignore-yml", new Boolean(false));
		} 
		

		if (config.getBoolean(SETTINGS_TAG + ".ignore-yml", false)) {
			IStick.log(Level.WARNING, "ignoring new configuration file; please consider migrating");
		}
	}
	
	public CSPermissionHandler getPermissionHandler(PluginManager manager) {
		CSPermissionHandler h = new CSPermissionHandler(manager, config.getBoolean(SETTINGS_TAG + ".allow-ops", ALLOW_OPS));
		
		if (CSPermissionHandler.usesOwnPermissions()) {
			List<String> users = config.getKeys(PERMISSIONS_TAG);
			if (users != null) {
				// a map of a List of users for each permission
				Map<String, String> perms = new HashMap<String,String>();
				for (String user : users) {
					IStick.log(Level.INFO, "setting permissions for " + user);
					List<String> confPerms = config.getStringList(PERMISSIONS_TAG + "." + user, new ArrayList<String>());
					for (String p : confPerms) {
						String u = perms.get(p);
						if (u == null)
							u = user;
						else
							u = u + "," + user;
						perms.put(p, u);
					}
				}
				
				for (String p : perms.keySet()) {
					CSPermissionHandler.add(p, perms.get(p));
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
			IStick.getLogger().log(Level.SEVERE, "Cannot generate config file", e);
		}	
	}

	private boolean loadOldConfig(File dir) {

		boolean oldConfigFound = false;
		
		// try to load the old settings
		PersistentProperty pprop = null;
		
		try {
			pprop = new PersistentProperty(dir, "iStick.permissions", false);
			oldConfigFound = true;
			IStick.log(Level.INFO, "Loading old permission file.");
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
					IStick.log(Level.INFO, "set permission for " + u + " to " + config.getList(PERMISSIONS_TAG + "." + u).get(0));
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
		return config.getBoolean(USER_SETTINGS_TAG + ".right-click-switch", PROTECT_BOTTOM);
	}

	public boolean isDebug() {
		return config.getBoolean(USER_SETTINGS_TAG + ".debug", DEBUG);
	}
}
