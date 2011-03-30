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

import java.util.Date;
import java.util.HashSet;
import java.util.Vector;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

import com.iminurnetz.bukkit.util.Item;
import com.iminurnetz.bukkit.util.MaterialUtils;

public class Stick {
	public static final int REMOVE_MODE = 0;
	public static final int REPLACE_MODE = 1;
	public static final int BUILD_MODE = 2;
	
	private int amount = 1;
	private int area = 0;
	private Vector<BlockState> blocks = new Vector<BlockState>();
	private int distance;
	private boolean drops;
	private boolean enabled;
	private HashSet<Byte> ignore = new HashSet<Byte>();
	private Item item = new Item(Material.AIR);
	private int mode = REMOVE_MODE;
	private boolean protectBottom;
	private boolean rightClickSwitch;
	private boolean debug;
	private Material tool;
	private int undoAmount;
	private boolean naturalDrops;
	private boolean announce;
	private int maxUndos;
	private long lastActionTakenAt;
	
    public Stick(ConfigurationService configService) {
		this.ignore = configService.getIgnored();
		this.enabled = configService.isEnabled();
		this.drops = configService.doesDrop();
		this.undoAmount = configService.getUndoAmount();
		this.distance = configService.getDistance();
		this.protectBottom = configService.doProtectBottom();
		this.rightClickSwitch = configService.doRightClickSwitch();
		this.naturalDrops = configService.doesNaturalDrops();
		this.announce = configService.doAnnounce();
		this.setDebug(configService.isDebug());
		this.tool = configService.getTool();
		this.maxUndos = configService.getMaxUndos();
		this.lastActionTakenAt = new Date().getTime();
	}

	public void addBlock(BlockState blockState) {
	    this.lastActionTakenAt = new Date().getTime();
		this.blocks.add(blockState);
		if (blocks.size() > maxUndos) {
		    blocks.remove(0);
		}
	}
	
	public boolean doAnnounce() {
		return announce;
	}

	public boolean doProtectBottom() {
		return protectBottom;
	}

	public void doProtectBottom(boolean protectBottom) {
		this.protectBottom = protectBottom;
	}

	public int getAmount() {
		return this.amount;
	}

	public int getArea() {
		return this.area;
	}

	public Vector<BlockState> getBlocks() {
		return this.blocks;
	}

	public int getDistance() {
		return this.distance;
	}

	public HashSet<Byte> getIgnore() {
		return this.ignore;
	}

	public Item getItem() {
		return this.item;
	}

	public int getMode() {
		return this.mode;
	}

	public String getModeAsString() {
		if (getItem().equals(Material.FLINT_AND_STEEL) && getMode() != REMOVE_MODE) {
			return "playing with fire";
		}
		
		return (getMode() == REMOVE_MODE ? "removing" : (getMode() == REPLACE_MODE ? "replacing" : "building"));
	}

	public Material getTool() {
		return this.tool;
	}

	public int getUndoAmount() {
		return undoAmount;
	}

	public void ignore(Material m) {
		if (m == null) return;
		this.ignore.add((byte) m.getId());
	}

	public boolean isDrops() {
		return this.drops;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public boolean doRightClickSwitch() {
		return rightClickSwitch;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public void setAnnounce(boolean bool) {
		this.announce = bool;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public void setDrops(boolean drops) {
		this.drops = drops;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean setItem(String item) {
		try {
			return setItem(new Item(item));
		} catch (InstantiationException e) {
			return false;
		}
	}

	public boolean setItem(Material material) {
		return setItem(material, material.getNewData((byte) 0));
	}
	
    public boolean setItem(Material material, MaterialData data) {
        return setItem(new Item(material, data));
    }

	public boolean setItem(Item item) {
	    doItemSwitch = false;
		if (!MaterialUtils.isSameItem(getItem(), item)) {
			this.item = item;
			return true;
		}
		
		return false;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}

	public void setRightClickSwitch(boolean rightClickSwitch) {
		this.rightClickSwitch = rightClickSwitch;
	}

	public boolean setTool(String item) {
		Material m = MaterialUtils.getMaterial(item);

		if (m == null) {
			return false;
		}

		this.tool = m;
		return true;
	}

	public void setUndoAmount(int n) {
		undoAmount = n;
	}

	public long getLastActionTakenAt() {
	    return lastActionTakenAt;
	}

	public String showSettings() {
		StringBuilder s = new StringBuilder();
		s.append("CreativeStick is currently ");
		s.append((isEnabled() ? "enabled" : "disabled"));
		s.append(" with the following settings:\n");
		s.append("Tool: ");
		s.append(getToolName());
		s.append(" | ");
		s.append("Mode: ");
		s.append(getModeAsString());
		s.append("\n");
		s.append("Drops: ");
		s.append(isDrops());
		s.append(" (dropping ");
		s.append(doesNaturalDrops() ? "naturally)" : "anything)");
		s.append("\n");
		s.append("Undos: default: ");
		s.append(getUndoAmount());
		s.append(" (available: ");
		s.append(getBlocks().size());
		s.append(")\n");
		s.append("Protect bottom: ");
		s.append(doProtectBottom());
		s.append("\n");
		s.append("Distance: ");
		s.append(getDistance());
		s.append("\n");
		s.append("Right-Click item switching: ");
		s.append(doRightClickSwitch());
		s.append("\n");
		s.append("Announcements: ");
		s.append(doAnnounce());
		s.append("\n");
		s.append("Debug mode: ");
		s.append(isDebug());
		s.append("\n");
		s.append("Currently ignored: ");
		s.append(MaterialUtils.getFormattedNameList(getIgnore()));
		s.append("\n");


		return s.toString();
	}
	
	public void toggle() {
		this.enabled = (!isEnabled());
	}

	public void toggleDrops() {
		this.drops = (!isDrops());
	}

	public void unignore(Material material) {
		if (material == null) return;
		this.ignore.remove((byte) material.getId());
	}

	public String getItemName() {
		return MaterialUtils.getFormattedName(getItem());
	}

	public String getToolName() {
		return MaterialUtils.getFormattedName(getTool());
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setNaturalDrops(boolean b) {
		this.naturalDrops = b;
	}
	
	public boolean doesNaturalDrops() {
		return naturalDrops;
	}

	private boolean doItemSwitch = false;
	
	public void setDoItemSwitch(boolean b) {
	    doItemSwitch = b && doRightClickSwitch();
	}
	
	public boolean doItemSwitch() {
		return doItemSwitch;
	}

	public void onlyIgnore(Material material) {
		if (material == null) return;
		this.ignore = new HashSet<Byte>();
		ignore(Material.AIR);
		ignore(material);
	}
}
