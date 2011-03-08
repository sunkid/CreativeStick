package com.iminurnetz.bukkit.permissions;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.bukkit.plugin.PluginManager;

import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissionHandlerService {
	private static final Logger log = Logger.getLogger("Minecraft");
	
	public static PermissionHandler getHandler(PluginManager manager) {
		
		PermissionHandler handler = null;
		
		if (manager.getPlugin("GroupManager") != null) {
			log.log(Level.INFO, "Using GroupManager permissions");
			GroupManager mgr = (GroupManager) manager.getPlugin("GroupManager");
			manager.enablePlugin(mgr);
			WorldsHolder wd = mgr.getWorldsHolder();
			GroupManagerPermissions gp = new GroupManagerPermissions(wd);
			handler = gp;
		} else if (manager.getPlugin("Permissions") != null) {
			log.log(Level.INFO, "Using Permissions' permissions");
			Permissions p = (Permissions) manager.getPlugin("Permissions");
			manager.enablePlugin(p);
			NijikokunPermissions np = new NijikokunPermissions();
			np.setHandler(p.getHandler());
			handler = np;
		} else {
			handler = getHandler(manager, false);
		}
		
		return handler;
	}

	public static PermissionHandler getHandler(PluginManager manager, boolean defaultPermission) {
		String verb = defaultPermission ? "allow" : "deny";
		log.log(Level.WARNING, "Setting default permissions to " + verb + " all actions");
		return new DefaultPermissions(defaultPermission);
	}
}
