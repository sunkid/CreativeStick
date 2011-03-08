package com.iminurnetz.bukkit.permissions;

import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.bukkit.entity.Player;

public class GroupManagerPermissions implements PermissionHandler {

	private WorldsHolder worldHolder;
	
	public GroupManagerPermissions(WorldsHolder wd) {
		this.worldHolder = wd;
	}

	@Override
	public boolean hasPermission(Player player, String permission) {
		return worldHolder.getWorldPermissions(player).permission(player, permission);
	}
}
