package com.iminurnetz.bukkit.plugin.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;

import com.iminurnetz.bukkit.plugin.BukkitPlugin;

public class PluginLogger {
	
	private BukkitPlugin plugin;
	private Server server;
	private String prefix = "";
	private Logger logger;

	private boolean inited = false;
	
	public PluginLogger(BukkitPlugin plugin) {
		this.plugin = plugin;
	}
	
	private void init() {
		server = plugin.getServer();
		prefix = "[" + plugin.getName() + " " + plugin.getVersion() + "] ";

		if (server != null)
			logger = server.getLogger();
		
		if (logger != null) {
			inited = true;
		}
	}
	
	public void log(Level level, String msg) {	
		if (!inited) {
			init();
		}
		
		if (!inited) {
			Logger.getLogger("Minecraft").log(level, prefix + msg);
			return;
		}

		logger.log(level, prefix + msg);
	}

	public void log(String msg) {
		log(Level.INFO, msg);
	}

	public Logger getLogger() {
		return logger;
	}

}
