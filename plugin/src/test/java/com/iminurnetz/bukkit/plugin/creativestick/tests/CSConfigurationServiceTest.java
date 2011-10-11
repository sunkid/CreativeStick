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

package com.iminurnetz.bukkit.plugin.creativestick.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.iminurnetz.bukkit.plugin.creativestick.CSConfigurationService;

public class CSConfigurationServiceTest extends TestCase {
    public void testVersion() throws FileNotFoundException, IOException, InvalidConfigurationException {
		URL topDir = getClass().getResource("/");
        File configFile = new File(topDir.getFile(), "../../src/main/resources/config.yml");
        YamlConfiguration config = new YamlConfiguration();
        config.load(configFile);
        assertEquals(config.getDouble("settings.version", -1), CSConfigurationService.LAST_CHANGED_IN_VERSION);
	}
}
