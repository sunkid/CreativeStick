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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.iminurnetz.bukkit.plugin.BukkitPlugin;
import com.iminurnetz.bukkit.plugin.util.ConfigurationService;
import com.iminurnetz.bukkit.util.MaterialUtils;

public class CSConfigurationService extends ConfigurationService {

	private FileConfiguration config;
	private BukkitPlugin plugin;
	
	// used to check the config file for updates
    public static final double LAST_CHANGED_IN_VERSION = 0.8;
	
	// TODO convert to enums
	// global settings
	public static final String SETTINGS_TAG = "settings";
	
	public static final boolean ALLOW_OPS = true;
	public static final boolean IS_ENABLED = false;
	public static final boolean DOES_DROP = true;
	
	// user settings
	public static final String USER_SETTINGS_TAG = SETTINGS_TAG + ".user";
	
	public static final int UNDO_AMOUNT = 5;
    public static final int MAX_UNDOS = 500;
	public static final int DISTANCE = 100;
	public static final Material TOOL = Material.STICK;
	public static final boolean PROTECT_BOTTOM = true;
    public static final boolean RIGHT_CLICK_SWITCH = false;
    public static final boolean RIGHT_CLICK_MODES = false;
	public static final boolean DEBUG = false;
	public static final boolean NATURAL_DROPS = false;
	public static final boolean ANNOUNCE = false;
    public static final boolean THROW_BUILD = false;
	
	// permissions
	public static final String PERMISSIONS_TAG = "permissions";
	
	public CSConfigurationService(BukkitPlugin plugin) {
        super(plugin, LAST_CHANGED_IN_VERSION);
		this.config = getConfiguration();
		this.plugin = plugin;
	}
	
	public CSPermissionHandler getPermissionHandler() {
        return new CSPermissionHandler(plugin, config.getBoolean(SETTINGS_TAG + ".allow-ops", ALLOW_OPS));
	}

	public boolean isEnabled() {
		return config.getBoolean(SETTINGS_TAG + ".enabled", IS_ENABLED);
	}

	public boolean doesDrop() {
		return config.getBoolean(SETTINGS_TAG + ".drops", DOES_DROP);
	}

	public int getUndoAmount() {
		return config.getInt(USER_SETTINGS_TAG + ".undo", UNDO_AMOUNT);
	}
	
	public int getDistance() {
		return config.getInt(USER_SETTINGS_TAG + ".distance", DISTANCE);
	}

	public Material getTool() {
		return MaterialUtils.getMaterial(config.getString(USER_SETTINGS_TAG + ".tool", TOOL.name()));
	}
	
	public boolean doProtectBottom() {
		return config.getBoolean(USER_SETTINGS_TAG + ".protect-bottom", PROTECT_BOTTOM);
	}

	public boolean doRightClickSwitch() {
		return config.getBoolean(USER_SETTINGS_TAG + ".right-click-switch", RIGHT_CLICK_SWITCH);
	}

    public boolean doRightClickModes() {
        return config.getBoolean(USER_SETTINGS_TAG + ".right-click-modes", RIGHT_CLICK_MODES);
    }

	public boolean isDebug() {
		return config.getBoolean(USER_SETTINGS_TAG + ".debug-mode", DEBUG);
	}

	public boolean doesNaturalDrops() {
		return config.getBoolean(USER_SETTINGS_TAG + ".natural-drops", NATURAL_DROPS);
	}
	
	public boolean doAnnounce() {
		return config.getBoolean(USER_SETTINGS_TAG + ".announce", ANNOUNCE);
	}

	public HashSet<Byte> getIgnored() {
        List<String> names = config.getList(SETTINGS_TAG + ".ignore", Collections.EMPTY_LIST);
		HashSet<Material> materials = new HashSet<Material>();
		for (String name : names) {
			Material m = MaterialUtils.getMaterial(name);
			if (m != null)
				materials.add(m);
		}
		
		HashSet<Byte> returnSet = new HashSet<Byte>();
		for (Material m : materials) {
			returnSet.add((byte) m.getId());
		}
		returnSet.add((byte) Material.AIR.getId());
		return returnSet;
	}

    public int getMaxUndos() {
        return config.getInt(USER_SETTINGS_TAG + ".max-undos", MAX_UNDOS);
    }

    public boolean useBlockSpawnPermission() {
        return config.getBoolean(SETTINGS_TAG + ".use-block-spawn-permission", false);
    }

    public boolean isThrowBuild() {
        return config.getBoolean(USER_SETTINGS_TAG + ".throw-build", THROW_BUILD);
    }
}
