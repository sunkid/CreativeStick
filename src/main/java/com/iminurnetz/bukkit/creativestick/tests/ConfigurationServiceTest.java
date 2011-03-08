package com.iminurnetz.bukkit.creativestick.tests;

import java.io.File;
import java.net.URL;

import org.bukkit.util.config.Configuration;

import com.iminurnetz.bukkit.creativestick.ConfigurationService;

import junit.framework.TestCase;

public class ConfigurationServiceTest extends TestCase {
	public void testVersion() {
		URL topDir = getClass().getResource("/");
		File configFile = new File(topDir.getFile(), "../src/main/resources/config.yml");
		Configuration config = new Configuration(configFile);
		config.load();
		assertEquals(config.getString("settings.version", ""), ConfigurationService.LAST_CHANGED_IN_VERSION);
	}
}
