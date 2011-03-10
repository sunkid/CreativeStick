package com.nijikokun.bukkit.istick;

import java.util.Vector;

import org.bukkit.Material;
import org.bukkit.block.BlockState;

import com.iminurnetz.bukkit.plugin.creativestick.ConfigurationService;
import com.iminurnetz.bukkit.util.MaterialUtils;

public class Stick {
	private int amount = 1;
	private int area = 0;
	private Vector<BlockState> blocks;
	private int distance;
	private boolean drops;
	private boolean enabled;
	private Vector<String> ignore;
	private Material item;
	private int mode = 0;
	private boolean protectBottom;
	private boolean rightClickSwitch;
	private boolean debug;
	private Material tool;
	private int undoAmount;
	private boolean useable;
	private boolean naturalDrops;
	
	public static final int REMOVE_MODE = 0;
	public static final int REPLACE_MODE = 1;
	public static final int BUILD_MODE = 2;
	

	public Stick(ConfigurationService configService) {
		this.blocks = new Vector<BlockState>();
		this.ignore = new Vector<String>();
		this.ignore.add("0");
		this.enabled = configService.isEnabled();
		this.drops = configService.doesDrop();
		this.undoAmount = configService.getUndoAmount();
		this.distance = configService.getDistance();
		this.protectBottom = configService.doProtectBottom();
		this.rightClickSwitch = configService.doRightClickSwitch();
		this.naturalDrops = configService.doesNaturalDrops();
		this.setDebug(configService.isDebug());
		this.tool = configService.getTool();
		this.useable = false;
		this.mode = REMOVE_MODE;
	}

	public void addBlock(BlockState blockState) {
		this.blocks.add(blockState);
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

	public Vector<String> getIgnore() {
		return this.ignore;
	}

	public Material getItem() {
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

	public void ignore(String block) {
		if (!this.ignore.contains(block))
			this.ignore.add(block);
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

	public void setItem(Material m) {
		this.item = m;
	}

	public boolean setItem(String item) {
		Material m = MaterialUtils.getPlaceableMaterial(item);

		if (m == null) {
			return false;
		}

		this.item = m;
		return true;
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

	public String showSettings() {
		StringBuilder s = new StringBuilder();
		s.append("CreativeStick is currently ");
		s.append((isEnabled() ? "enabled" : "disabled"));
		s.append(" with the following settings:\n");
		s.append("Tool: ");
		s.append(getToolName());
		s.append("\n");
		s.append("Mode: ");
		s.append(getModeAsString());
		s.append("\n");
		s.append("Drops: ");
		s.append(isDrops());
		s.append("\n");
		s.append("Undos: default: ");
		s.append(getUndoAmount());
		s.append(" (available: ");
		s.append(getBlocks().size() -1);
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
		s.append("Debug mode: ");
		s.append(isDebug());
		s.append("\n");


		return s.toString();
	}
	
	public void toggle() {
		this.enabled = (!isEnabled());
	}

	public void toggleDrops() {
		this.drops = (!isDrops());
	}

	public void unignore(String block) {
		if (this.ignore.contains(block))
			this.ignore.remove(block);
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

	public boolean isUseable() {
		return useable ;
	}
	
	public void setUseable(boolean useable) {
		this.useable = useable;
	}

	public void setNaturalDrops(boolean b) {
		this.naturalDrops = b;
	}
	
	public boolean doesNaturalDrops() {
		return naturalDrops;
	}
}
