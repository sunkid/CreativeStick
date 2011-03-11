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

package com.iminurnetz.bukkit.plugin.creativestick;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import com.iminurnetz.util.EnumUtil;

public enum CreativeStickCommand {

	HELP("-h", "Shows help information", "[command]", CSPermissionHandler.CAN_USE_PERMISSION),
	TOGGLE("-t", "Toggle CreativeStick on / off", "", CSPermissionHandler.CAN_USE_PERMISSION),
	TOGGLE_MODE("-tm", "Toggle between modes", "<item>", CSPermissionHandler.CAN_USE_PERMISSION),
	BUILD("-b", "Switch to building mode", "<item>", CSPermissionHandler.CAN_USE_PERMISSION),
	REPLACE("-r", "Switch to replace mode", "<item>", CSPermissionHandler.CAN_USE_PERMISSION),
	IGNORE("-i", "Set transparent blocks", "[+|-]<block>[, <+|-><block>]", CSPermissionHandler.CAN_USE_PERMISSION),
	TOGGLE_DROPS("-td", "Toggle drops on / off", "", CSPermissionHandler.CAN_USE_PERMISSION),
	UNDO("-u", "Undo previous action(s)", "<n>|'all'", CSPermissionHandler.CAN_USE_PERMISSION),
	// TODO special case with dual permissions; may have to rethink
	CONFIG("-c", "Show or change config values", "[<param> <value>]", CSPermissionHandler.CAN_USE_PERMISSION),
	VERSION("-v", "Display the current version.", "", CSPermissionHandler.CAN_USE_PERMISSION);

	public static final String CREATIVE_STICK_COMMAND = "cs";

	private String synonym;
	private String helpText;
	private String parameters;
	private String permission;
	
	private CreativeStickCommand(String synonym, String helpText, String parameters, String permission) {
		this.synonym = synonym;
		this.helpText = helpText;
		this.parameters = parameters;
		this.permission = permission;
	}

	// used for formatting of the help text
	private static int longestCmd = 0;

	static {
		for (CreativeStickCommand cmd : CreativeStickCommand.values())			
			CreativeStickCommand.longestCmd = Math.max(CreativeStickCommand.longestCmd,
													   cmd.name().length() + cmd.getParameters().length() + 4);
	}
	
	/**
	 * @return the synonym for this command
	 */
	public String getSynonym() {
		return synonym;
	}

	/**
	 * @return the help text for this command
	 */
	public String getHelpText() {
		return helpText;
	}

	/**
	 * @return the parameters that can be used with this command
	 */
	public String getParameters() {
		return parameters;
	}

	/**
	 * @return the permission required for this command
	 */
	public String getPermission() {
		return permission;
	}
	
	@Override
	public String toString() {
		String param = " " + getParameters();
		if (param.length() == 1)
			param = "";
		
		String cmd = getSynonym() + "|" + EnumUtil.getSerializationName(this) + param;
		// need to get fixed size fonts to work here
		return cmd /*+ StringUtils.repeat(" ", CreativeStickCommand.longestCmd - cmd.length())*/ + " - " + getHelpText();
	}
	
	/**
	 * Retrieve a command by either its name or synonym
	 * @param command The command or synonym
	 * @return the CreativeStickCommand corresponding to the command or synonym
	 */
	public static CreativeStickCommand getCommand(String command) {
		for (CreativeStickCommand cmd : CreativeStickCommand.values()) {
			if (EnumUtil.getSerializationName(cmd).equalsIgnoreCase(command) ||
					cmd.getSynonym().equalsIgnoreCase(command))
				return cmd;
		}
		
		return CreativeStickCommand.HELP;
	}
	
	/**
	 * Produces a player-specific help text.
	 * @param player the player to display the help text for
	 * @param command the actual command used (may be alias)
	 * @return the text describing the commands available to the player
	 */
	public static String getHelp(Player player, Command command) {
		StringBuilder help = new StringBuilder();
		
		help.append("Usage: /");
		help.append(command.getName());
		for (String alias : command.getAliases()) {
			help.append("|");
			help.append(alias);
		}
		help.append(" [command] [parameters]\n");
				
		for (CreativeStickCommand cmd : CreativeStickCommand.values()) {
			if (CSPlugin.permissionHandler.hasPermission(player, cmd.getPermission())) {
				help.append("   " + cmd);
				help.append("\n");
			}
		}
		
		if (help.length() == 0)
			help.append("This command is not available for you.");
		
		return help.toString();
	}

	public String showAccessDenied() {
		return ChatColor.RED + "/" + CREATIVE_STICK_COMMAND + " " + EnumUtil.getSerializationName(this) + ": access denied!";

	}
}
