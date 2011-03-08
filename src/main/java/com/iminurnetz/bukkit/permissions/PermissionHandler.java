package com.iminurnetz.bukkit.permissions;

import org.bukkit.entity.Player;

public interface PermissionHandler {
	public boolean hasPermission(Player player, String permission);
}
