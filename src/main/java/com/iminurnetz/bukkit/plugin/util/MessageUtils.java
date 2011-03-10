package com.iminurnetz.bukkit.plugin.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtils {
	public static String convertColor(String original) {
		Pattern p = Pattern.compile("&([a-z0-9])");
		String result = new String(original);
		Matcher m = p.matcher(original);
		
		while (m.find()) {
			result = result.replaceAll(m.group(), ChatColor.getByCode(toInt(m.group(1))).toString());
		}
		
		return result;
	}

	private static int toInt(String string) {
		String s = string.trim();
		if (s.length() > 1)
			return -1;
		try {
			return Integer.parseInt(s, 16);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public static void send(Player player, String message) {
		send(player, ChatColor.WHITE, message);
	}
	
	public static void send(Player player, ChatColor color, String message) {
		for (String line : message.split("\n"))
			player.sendMessage(line);
	}

	public static String colorize(ChatColor color, String string) {
		if (color.equals(ChatColor.WHITE))
			return string;
		return color + string + ChatColor.WHITE;
	}
}