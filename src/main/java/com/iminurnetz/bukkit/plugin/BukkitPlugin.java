package com.iminurnetz.bukkit.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.ConfigurationException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;

import com.iminurnetz.bukkit.plugin.util.MessageUtils;
import com.iminurnetz.bukkit.plugin.util.PluginLogger;
import com.nijikokun.bukkit.istick.IStick;

public abstract class BukkitPlugin extends JavaPlugin {
	protected Map<String, Object> root;
	protected PluginLogger logger;

	public BukkitPlugin() {
		logger = new PluginLogger(this);

		// we are loading plugin.yml ourselves
		URL configUrl = getClass().getResource("/plugin.yml");
		Yaml yaml = new Yaml(new SafeConstructor(), new Representer());
		InputStream stream = null;

		try {
			stream = configUrl.openStream();
			read(yaml.load(new UnicodeReader(stream)));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot access plugin.yml");
		} catch (ConfigurationException e) {
			logger.log(Level.SEVERE, "Cannot access plugin.yml");
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
			}
		}

		logger.log("loaded");
	}

	public PluginLogger getLogger() {
		return logger;
	}

	// simple shortcut
	public void log(String msg) {
		logger.log(msg);
	}

	// simple shortcut
	public void log(Level level, String msg) {
		logger.log(level, msg);
	}

	public String getName() {
		try {
			return root.get("name").toString();
		} catch (Exception e) {
			logger.log(Level.SEVERE, this.getClass().getSimpleName()
					+ " has no name yet!");
		}

		return "";
	}

	public String getVersion() {
		try {
			return root.get("version").toString();
		} catch (Exception e) {
			logger.log(Level.SEVERE, this.getClass().getSimpleName()
					+ " has no version yet!");
		}

		return "";
	}

	@SuppressWarnings("unchecked")
	private void read(Object input) throws ConfigurationException {
		try {
			root = (Map<String, Object>) input;
		} catch (ClassCastException e) {
			throw new ConfigurationException(
					"Root document must be an key-value structure");
		}
	}

	public String getFullMessagePrefix() {
		return getFullMessagePrefix(ChatColor.WHITE);
	}
	
	public String getFullMessagePrefix(ChatColor color) {
		return MessageUtils.colorize(color, "[" + getName() + " " + getVersion() + "] ");
	}
	
	public String getMessagePrefix() {
		return "[" + getName() + "] ";
	}
	
	public String getMessagePrefix(ChatColor color) {
		return MessageUtils.colorize(color, getMessagePrefix());
	}

}