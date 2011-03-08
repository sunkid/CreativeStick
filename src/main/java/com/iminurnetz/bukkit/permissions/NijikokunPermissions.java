package com.iminurnetz.bukkit.permissions;

import org.bukkit.entity.Player;

public class NijikokunPermissions implements PermissionHandler {

	private com.nijiko.permissions.PermissionHandler handler;
	
	protected void setHandler(com.nijiko.permissions.PermissionHandler handler) {
		this.handler = handler;
	}

	@Override
	public boolean hasPermission(Player player, String permission) {
		return handler.permission(player, permission);
	}

}
