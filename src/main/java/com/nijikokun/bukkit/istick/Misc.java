package com.nijikokun.bukkit.istick;

import java.io.File;
import java.io.IOException;

import org.bukkit.entity.Player;


public class Misc {
	public static Boolean arguments(String[] array, int amount) {
		return Boolean.valueOf(array.length < amount + 2);
	}

	public static Boolean is(String text, String against) {
		return Boolean.valueOf(text.equalsIgnoreCase(against));
	}

	public static Boolean isEither(String text, String against, String or) {
		return Boolean.valueOf((text.equalsIgnoreCase(against))
				|| (text.equalsIgnoreCase(or)));
	}

	public static String formatCurrency(int Balance, String currency) {
		return insertCommas(String.valueOf(Balance)) + " " + currency;
	}

	public static String insertCommas(String str) {
		if (str.length() < 4) {
			return str;
		}

		return insertCommas(str.substring(0, str.length() - 3)) + ","
				+ str.substring(str.length() - 3, str.length());
	}

	public static String string(int i) {
		return String.valueOf(i);
	}

	public static boolean validate(String name) {
		return name.matches("([0-9a-zA-Z._-]+)");
	}

	public static String repeat(char c, int i) {
		String tst = "";
		for (int j = 0; j < i; j++) {
			tst = tst + c;
		}
		return tst;
	}

	public static Player player(String name) {
		if (IStick.server.getOnlinePlayers().length < 1) {
			return null;
		}

		Player[] online = IStick.server.getOnlinePlayers();
		Player player = null;

		for (Player needle : online) {
			if (needle.getName().equals(name)) {
				player = needle;
				break;
			}
			if (needle.getDisplayName().equals(name)) {
				player = needle;
				break;
			}
		}

		return player;
	}

	public static Player playerMatch(String name) {
		if (IStick.server.getOnlinePlayers().length < 1) {
			return null;
		}

		Player[] online = IStick.server.getOnlinePlayers();
		Player lastPlayer = null;

		for (Player player : online) {
			String playerName = player.getName();
			String playerDisplayName = player.getDisplayName();

			if (playerName.equalsIgnoreCase(name)) {
				lastPlayer = player;
				break;
			}
			if (playerDisplayName.equalsIgnoreCase(name)) {
				lastPlayer = player;
				break;
			}

			if (playerName.toLowerCase().indexOf(name.toLowerCase()) != -1) {
				if (lastPlayer != null) {
					return null;
				}

				lastPlayer = player;
			} else if (playerDisplayName.toLowerCase().indexOf(
					name.toLowerCase()) != -1) {
				if (lastPlayer != null) {
					return null;
				}

				lastPlayer = player;
			}
		}

		return lastPlayer;
	}

	public static void touch(File directory, String name) {
		try {
			new File(directory, name).createNewFile();
		} catch (IOException ex) {
		}
	}

	public static void touch(String name) {
		try {
			new File(name).createNewFile();
		} catch (IOException ex) {
		}
	}

	public static class string {
		public static String combine(int startIndex, String[] string,
				String seperator) {
			StringBuilder builder = new StringBuilder();

			for (int i = startIndex; i < string.length; i++) {
				builder.append(string[i]);
				builder.append(seperator);
			}

			builder.deleteCharAt(builder.length() - seperator.length());
			return builder.toString();
		}

		public static String capitalize(String s) {
			return s.toUpperCase().charAt(0) + s.toLowerCase().substring(1);
		}

		public static String camelToPhrase(String str) {
			String newStr = "";
			for (int i = 0; i < str.length(); i++) {
				if ((i > 0) && (Character.isUpperCase(str.charAt(i)))
						&& (!Character.isUpperCase(str.charAt(i - 1)))) {
					newStr = newStr + ' ';
				}
				newStr = newStr + str.charAt(i);
			}
			return newStr;
		}
	}
}