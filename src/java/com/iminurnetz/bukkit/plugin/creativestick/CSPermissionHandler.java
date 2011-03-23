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

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.iminurnetz.bukkit.permissions.DefaultPermissions;
import com.iminurnetz.bukkit.permissions.PermissionHandler;
import com.iminurnetz.bukkit.permissions.PermissionHandlerService;
import com.iminurnetz.bukkit.plugin.BukkitPlugin;

public class CSPermissionHandler implements PermissionHandler {
	
	public static final String CAN_USE_PERMISSION = "creativestick.use";
	public static final String CAN_CONFIGURE_PERMISSION = "creativestick.config";
	public static final String CAN_DO_EVERYTHING = "creativestick.*";
	
	private static HashMap<String, ArrayList<String>> permissions = new HashMap<String, ArrayList<String>>();

	private PermissionHandler permissionHandler;
	private boolean useOwnPermissions = false;
	private boolean enableOps = ConfigurationService.ALLOW_OPS;

	protected CSPermissionHandler(BukkitPlugin plugin, boolean enableOps) {
		permissionHandler = PermissionHandlerService.getHandler(plugin);
		if (permissionHandler instanceof DefaultPermissions) {
			((DefaultPermissions) permissionHandler).enableOps(enableOps);		
			useOwnPermissions = true;
		}
	}
	
	protected boolean usesOwnPermissions() {
		return useOwnPermissions;
	}
	
	protected void addPermission(String player, String permission) {
		ArrayList<String> perms = permissions.get(player);
		if (perms == null)
			perms = new ArrayList<String>();
		
		if (permission.equals(CAN_DO_EVERYTHING))
			perms.removeAll(null);
		
		if (!perms.contains(permission) && !perms.contains(CAN_DO_EVERYTHING))
			perms.add(permission);
		
		permissions.put(player, perms);
	}

	public boolean permission(Player player, String permission) {
		String name = player.getName();
		
		if (!permissions.containsKey(name)) {
			return false;
		}

		return permissions.get(name).contains(permission) || permissions.get(name).contains(CAN_DO_EVERYTHING);
	}

	public boolean hasPermission(Player player, String permission) {
		if (enableOps && player.isOp())
			return true;
		
		return usesOwnPermissions() ? 
				permission(player, permission) :
				permissionHandler.hasPermission(player, permission);
	}

	public boolean canUse(Player player) {
		return (hasPermission(player, CAN_USE_PERMISSION) || canDoEverything(player));
	}
	
	public boolean canUse(Player player, CreativeStickCommand command) {
		return (hasPermission(player, command.getPermission()) || canDoEverything(player));
	}
	
	public boolean canConfigureAll(Player player) {
		return (hasPermission(player, CAN_CONFIGURE_PERMISSION)
				|| hasPermission(player, CAN_CONFIGURE_PERMISSION + ".*")
				|| canDoEverything(player));
	}

	public boolean canDoEverything(Player player) {
		return hasPermission(player, CAN_DO_EVERYTHING);
	}

	public boolean canConfigure(Player player, String param) {
		return canConfigureAll(player) || hasPermission(player, CAN_CONFIGURE_PERMISSION + "." + param.toLowerCase());
	}

}
