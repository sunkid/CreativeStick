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

import java.io.File;
import java.io.IOException;


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