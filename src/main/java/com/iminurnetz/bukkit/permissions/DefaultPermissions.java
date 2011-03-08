package com.iminurnetz.bukkit.permissions;

import org.bukkit.entity.Player;

public class DefaultPermissions implements PermissionHandler {

	private boolean defaultPermission = false;
	private boolean enableOps = true;
	
	public DefaultPermissions(boolean defaultPermission) {
		this.defaultPermission = defaultPermission;
	}
	
	public void allowEverything() {
		defaultPermission = true;
	}
	
	public void denyEverything() {
		defaultPermission = false;
	}
	
	public void enableOps(boolean bool) {
		this.enableOps = bool;
	}
	
	public boolean hasPermission(Player player, String permission) {
		return (defaultPermission || enableOps);
	}

}
