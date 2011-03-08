package com.nijikokun.bukkit.istick;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Messaging {
	public static Player player = null;

	public static String argument(String original, String[] arguments,
			String[] points) {
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i].contains(",")) {
				for (String arg : arguments[i].split(","))
					original = original.replace(arg, points[i]);
			} else {
				original = original.replace(arguments[i], points[i]);
			}
		}

		return original;
	}

	public static String parse(String original) {
		return original.replaceAll("(&([a-z0-9]))", "§$2").replace("&&", "&");
	}

	public static String colorize(String original) {
		return original.replace("<black>", "§0").replace("<navy>", "§1")
				.replace("<green>", "§2").replace("<teal>", "§3").replace(
						"<red>", "§4").replace("<purple>", "§5").replace(
						"<gold>", "§6").replace("<silver>", "§7").replace(
						"<gray>", "§8").replace("<blue>", "§9").replace(
						"<lime>", "§a").replace("<aqua>", "§b").replace(
						"<rose>", "§c").replace("<pink>", "§d").replace(
						"<yellow>", "§e").replace("<white>", "§f");
	}

	public static String bracketize(String message) {
		return "[" + message + "]";
	}

	public static void send(Player player, String message) {
		for (String line : message.split("\n"))
			player.sendMessage(line);
	}


	public static void broadcast(String message) {
		for (Player p : IStickPlayerListener.plugin.getServer().getOnlinePlayers())
			p.sendMessage(parse(message));
	}

	public static String getMsgFront(ChatColor color) {
		return color + bracketize(IStick.version.getProject()) + ChatColor.WHITE + " ";
	}
}