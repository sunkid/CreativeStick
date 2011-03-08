/**
 * LICENSING
 * 
 * This software is copyright by sunkid <sunkid.com> and is distributed under a dual license:
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
 *    Please contact sunkid.com
 */

package com.iminurnetz.bukkit.creativestick;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.iminurnetz.bukkit.permissions.DefaultPermissions;
import com.iminurnetz.bukkit.permissions.PermissionHandler;
import com.iminurnetz.bukkit.permissions.PermissionHandlerService;
import com.nijikokun.bukkit.istick.Messaging;

public class CSPermissionHandler implements PermissionHandler {
	
	public static final String CAN_USE_PERMISSION = "creativestick.use";
	public static final String CAN_CONFIGURE_PERMISSION = "creativestick.config";
	public static final String CAN_DO_EVERYTHING = "creativestick.*";
	
	public static final String NOT_ALLOWED = Messaging.getMsgFront(ChatColor.RED) + " is not available for you.";
	
	private static HashMap<String, String> permissions = new HashMap<String, String>();

	private static PermissionHandler permissionHandler;
	private static boolean useOwnPermissions = false;
	private static boolean enableOps = ConfigurationService.ALLOW_OPS;

	protected CSPermissionHandler(PluginManager manager, boolean enableOps) {
		permissionHandler = PermissionHandlerService.getHandler(manager);
		if (permissionHandler instanceof DefaultPermissions) {
			((DefaultPermissions) permissionHandler).enableOps(enableOps);		
			useOwnPermissions = true;
		}
	}
	
	protected static boolean usesOwnPermissions() {
		return useOwnPermissions;
	}
	
	protected static void add(String controller, String groups) {
		permissions.put(controller, groups);
	}

	private static boolean permission(String controller, Player player) {
		
		if (!permissions.containsKey(controller)) {
			return false;
		}

		String groups = (String) permissions.get(controller);

		if (!groups.equals("*")) {
			String[] groupies = groups.split(",");

			for (String group : groupies) {
				if (player.getName().equals(group)) {
					return true;
				}
			}

			return false;
		}

		return true;
	}

	public boolean hasPermission(Player player, String permission) {
		if (enableOps && player.isOp())
			return true;
		
		if (CSPermissionHandler.useOwnPermissions)
			return CSPermissionHandler.permission(permission, player);
		else
			return permissionHandler.hasPermission(player, permission);
	}

	public boolean canUse(Player player) {
		return (hasPermission(player, CAN_USE_PERMISSION) || canDoEverything(player));
	}
	
	public boolean canUse(Player player, CreativeStickCommand command) {
		return (hasPermission(player, command.getPermission()) || canDoEverything(player));
	}
	
	public boolean canConfigure(Player player) {
		return (hasPermission(player, CAN_CONFIGURE_PERMISSION) || canDoEverything(player));
	}

	public boolean canDoEverything(Player player) {
		return hasPermission(player, CAN_DO_EVERYTHING);
	}

}
