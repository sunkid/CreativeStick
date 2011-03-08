package com.iminurnetz.bukkit.creativestick.tests;

import com.iminurnetz.bukkit.creativestick.CreativeStickCommand;

import junit.framework.TestCase;

public class CreativeStickCommandTest extends TestCase {
	public void testToString() {
		assertEquals("-v|version - Display the current version.", CreativeStickCommand.VERSION.toString().replaceAll("\\s+", " "));
	}
}
