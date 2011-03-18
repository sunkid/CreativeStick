package com.nijikokun.bukkit.istick;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.Cancellable;
import org.bukkit.material.MaterialData;

import com.iminurnetz.bukkit.plugin.creativestick.ConfigurationService;
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
	
	private HashMap<Cancellable, BlockStateForSwitching> actionQueue = new HashMap<Cancellable, BlockStateForSwitching>();

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
	}

	public void addBlock(BlockState blockState) {
		this.blocks.add(blockState);
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
			setItem(new Item(item));
		} catch (InstantiationException e) {
			return false;
		}
		
		return true;
	}

	public void setItem(Material material) {
		setItem(material, material.getNewData((byte) 0));
	}
	
	public void setItem(Item item) {
		if (!MaterialUtils.isSameItem(getItem(), item)) {
			this.item = item;
			didItemSwitch = true;
		} else {
			didItemSwitch = false;
		}
	}
	
	public void setItem(Material material, MaterialData data) {
		setItem(new Item(material, data));
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

	/**
	 * Attempts to place a BlockState into this stick's action queue.
	 * @param state the BlockState for the user's queued action
	 * @return true on success, false otherwise
	 */
	public synchronized void enqueue(Cancellable event, BlockState state) {
		enqueue(event, state, false);
	}
	
	private boolean didItemSwitch = false;
	
	/**
	 * Attempts to place a BlockState into this stick's action queue and also specifies whether
	 * to use it as the source for the next item to use. It actually does not switch the item
	 * in use but delays that decision until dequeue is called.
	 * @param state the BlockState for the user's queued action
	 * @param forItemSwitching whether to use this blocks material for future actions
	 * @return true on success, false otherwise
	 */
	public synchronized void enqueue(Cancellable event, BlockState state, boolean forItemSwitching) {
		actionQueue.put(event, new BlockStateForSwitching(state, forItemSwitching && doRightClickSwitch()));
	}

	/**
	 * Returns the next BlockState to act on. It also attempts to switch to this item if the enqueue
	 * method was told to do so.
	 * @param event 
	 * @return the BlockState next in line or null if there are none.
	 */
	public synchronized BlockState dequeue(Cancellable event) {
		BlockStateForSwitching bs4s = actionQueue.remove(event);
		if (bs4s == null) {
			return null;
		}
		
		BlockState state = bs4s.state;
		
		Material blockMaterial = state.getBlock().getState().getType();
		
		didItemSwitch = bs4s.doSwitch;
		
		if (blockMaterial != null && bs4s.doSwitch) {
			setItem(blockMaterial, state.getBlock().getState().getData());
		}
		return state;
	}
	

	
	public boolean didItemSwitch() {
		return didItemSwitch;
	}

	public void onlyIgnore(Material material) {
		if (material == null) return;
		this.ignore = new HashSet<Byte>();
		ignore(Material.AIR);
		ignore(material);
	}
}

class BlockStateForSwitching {
	protected BlockState state;
	protected boolean doSwitch;
	protected BlockStateForSwitching(BlockState state, boolean doSwitch) {
		this.state = state;
		this.doSwitch = doSwitch;
	}
}
