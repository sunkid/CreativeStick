package com.iminurnetz.bukkit.plugin.util.tests;

import junit.framework.TestCase;

import org.bukkit.ChatColor;

import com.iminurnetz.bukkit.plugin.util.MessageUtils;


public class MessageUtilsTest  extends TestCase {
	public void testConvertColor() {
		String original = getOldCode(ChatColor.AQUA) + "Hello World" + getOldCode(ChatColor.BLACK);
		String expected = ChatColor.AQUA.toString() + "Hello World" + ChatColor.BLACK.toString();
		assertEquals(expected, MessageUtils.convertColor(original));
	}
	
	private String getOldCode(ChatColor color) {
		return "&" + Integer.toHexString( 0x10 | color.getCode()).substring(1).toLowerCase();
	}
}
