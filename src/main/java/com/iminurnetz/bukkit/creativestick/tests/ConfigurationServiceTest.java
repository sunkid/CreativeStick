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
