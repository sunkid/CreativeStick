package com.iminurnetz.bukkit.plugin.creativestick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import com.iminurnetz.bukkit.plugin.BukkitPlugin;
import com.iminurnetz.bukkit.plugin.util.MessageUtils;
import com.iminurnetz.bukkit.plugin.util.PluginLogger;
import com.iminurnetz.bukkit.util.InventoryUtil;
import com.iminurnetz.bukkit.util.MaterialUtils;
import com.iminurnetz.util.PersistentProperty;
import com.iminurnetz.util.StringUtils;
import com.nijikokun.bukkit.istick.Stick;

public class CSPlugin extends BukkitPlugin {
	protected static PluginLogger logger;

	private final PlayerListener playerListener = new CSPlayerListener(this);
	private final BlockListener undoListener = new CSEventListener(this);

	public static CSPermissionHandler permissionHandler;
	protected ConfigurationService configLoader;

	private static HashMap<Player, Stick> sticks = new HashMap<Player, Stick>();

	public static PersistentProperty settings;

	public static ArrayList<Player> drop = new ArrayList<Player>();
	protected static Server server;

	public CSPlugin() {
		super();
		logger = getLogger();
	}

	@Override
	public void onDisable() {
		logger.log("un-loaded");
	}

	@Override
	public void onEnable() {
		setup();
		registerEvents();
	}

	private void registerEvents() {
		PluginManager pm = server.getPluginManager();

		pm.registerEvent(Event.Type.PLAYER_ANIMATION, playerListener, Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_ITEM_HELD, playerListener, Priority.Monitor, this);
		// pm.registerEvent(Event.Type.BLOCK_BREAK, undoListener,
		// Priority.Monitor, this);
		pm.registerEvent(Event.Type.BLOCK_PLACED, undoListener, Priority.Highest, this);
	}

	public void setup() {
		server = getServer();
		configLoader = new ConfigurationService(this);
		permissionHandler = configLoader.getPermissionHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		Player player = null;

		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			sender.sendMessage("CreativeStick is currently only accessible in-game");
			return false;
		}

		Stick stick = null;

		String c = args.length >= 1 ? args[0] : "help";
		CreativeStickCommand cmd = CreativeStickCommand.getCommand(c);

		if (!permissionHandler.hasPermission(player, cmd.getPermission())) {
			MessageUtils.send(player, cmd.showAccessDenied());
			return true;
		}

		stick = getStick(player);
		boolean success = true;

		switch (cmd) {
		case HELP:
			MessageUtils.send(player, CreativeStickCommand.getHelp(player, command));
			break;

		case VERSION:
			StringBuilder msg = new StringBuilder(getName());
			msg.append(" v");
			msg.append(getVersion());
			msg.append("\nWebsite: ");
			msg.append(getDescription().getWebsite());
			msg.append("\nAuthors: ");
			for (String author : getDescription().getAuthors()) {
				msg.append(author);
				msg.append(", ");
			}

			MessageUtils.send(player, msg.substring(0, msg.length() - 2));
			break;

		case TOGGLE_DROPS:
			stick.toggleDrops();
			MessageUtils.send(player, "Drops have been " + (stick.isDrops() ? "enabled" : "disabled") + ".");
			break;

		case TOGGLE:
			toggle(player, stick);
			break;

		case TOGGLE_MODE:
			success = toggleMode(player, stick, getItemFromArgs(args));
			break;

		case BUILD:
			success = toggleBuild(player, stick, getItemFromArgs(args));
			break;

		case REPLACE:
			success = toggleReplace(player, stick, getItemFromArgs(args));
			break;
			
		case IGNORE:
			doIgnore(player, stick, args);
			break;

		case UNDO:
			doUndo(player, stick, args.length > 1 ? args[1] : "");
			break;

		case CONFIG:
			success = doConfig(player, stick, args);
			break;

		default:
			success = false;
		}

		if (!success)
			MessageUtils.send(player, ChatColor.RED + "Usage: /" + CreativeStickCommand.CREATIVE_STICK_COMMAND + " " + cmd);

		return true;
	}

	public void toggle(Player player, Stick stick) {
		stick.toggle();
		MessageUtils.send(player, getName() + " has been " + (stick.isEnabled() ? "enabled" : "disabled") + ".");
		checkForTool(player, stick);
	}
	
	private void checkForTool(Player player, Stick stick) {
		if (stick.isEnabled() && permissionHandler.canUse(player)) {
			// free sticks!! YEAH!!
			if (!player.getInventory().contains(stick.getTool())) {
				InventoryUtil.giveItems(player, stick.getTool(), player.getInventory().getHeldItemSlot());
				if (stick.isDebug()) {
					MessageUtils.send(player, "You just received a " + MaterialUtils.getFormattedName(stick.getTool())
							+ " for use with " + getName());
				}
			}
			
			InventoryUtil.switchToItems(player, stick.getTool());
		}
	}
	
	public void doIgnore(Player player, Stick stick, String[] originalArgs) {
		String[] commandArgs = new String[originalArgs.length - 1];
		for (int n = 0; n < commandArgs.length; n++) {
			commandArgs[n] = originalArgs[n + 1];
		}
		String value = StringUtils.join(" ", commandArgs);

		String[] args = value.split(",");
		for (String arg : args) {
			if (arg.startsWith("+")) {
				stick.ignore(MaterialUtils.getMaterial(arg.substring(1)));
			} else if (arg.startsWith("-")) {
				stick.unignore(MaterialUtils.getMaterial(arg.substring(1)));
			} else {
				stick.onlyIgnore(MaterialUtils.getMaterial(arg));
			}
		}
		
		MessageUtils.send(player, "You are now ignoring: " + MaterialUtils.getFormattedNameList(stick.getIgnore()));
	}
	
	public boolean doConfig(Player player, Stick stick, String[] originalArgs) {
		if (originalArgs.length < 2) {
			MessageUtils.send(player, stick.showSettings());
			return true;
		}

		String param = originalArgs[1];

		if (!permissionHandler.canConfigure(player, param)) {
			MessageUtils.send(player, CreativeStickCommand.CONFIG.showAccessDenied());
			return true;
		}

		String[] commandArgs = new String[originalArgs.length - 2];
		for (int n = 0; n < commandArgs.length; n++) {
			commandArgs[n] = originalArgs[n + 2];
		}
		
		String value = StringUtils.join(" ", commandArgs);

		if (param.equalsIgnoreCase("distance")) {
			int distance = stick.getDistance();
			try {
				distance = Integer.valueOf(value).intValue();
			} catch (Exception e) {
				MessageUtils.send(player, "Invalid distance given!");
				return false;
			}

			if (distance != stick.getDistance()) {
				stick.setDistance(distance);
				MessageUtils.send(player, "Distance changed to " + distance + ".");
			}

			return true;
		}

		if (param.equalsIgnoreCase("tool")) {
			if (!stick.setTool(value)) {
				MessageUtils.send(player, "Bad tool name or id.");
				return false;
			}
			MessageUtils.send(player, "Your tool has been changed to " + stick.getToolName() + ".");
			return true;
		}

		if (param.equalsIgnoreCase("protect-bottom")) {
			boolean protectBottom = StringUtils.isTrue(value);
			stick.doProtectBottom(protectBottom);

			if (protectBottom)
				MessageUtils.send(player, "Your bottom is protected!");
			else
				MessageUtils.send(player, "Your bottom is no longer protected!");

			return true;
		}

		if (param.equalsIgnoreCase("natural-drops")) {
			boolean naturalDrops = StringUtils.isTrue(value);
			stick.setNaturalDrops(naturalDrops);

			if (naturalDrops)
				MessageUtils.send(player, "You will now only receive naturally dropped items!");
			else
				MessageUtils.send(player, "You will get what you hit!");

			return true;
		}

		if (param.equalsIgnoreCase("right-click-switch")) {
			boolean doRightClickSwitch = StringUtils.isTrue(value);
			stick.setRightClickSwitch(doRightClickSwitch);

			if (doRightClickSwitch)
				MessageUtils.send(player, "Right-Clicking now selects!");
			else
				MessageUtils.send(player, "Right-Clicking no longer selects");

			return true;
		}

		if (param.equalsIgnoreCase("undo")) {
			int undo;
			try {
				undo = Integer.valueOf(value).intValue();
			} catch (NumberFormatException e) {
				MessageUtils.send(player, "Invalid number given!");
				return false;
			}

			stick.setUndoAmount(undo);
			MessageUtils.send(player, "Set undos to " + undo);
			return true;
		}

		if (param.equalsIgnoreCase("debug")) {
			boolean doDebug = StringUtils.isTrue(value);
			stick.setDebug(doDebug);

			if (doDebug)
				MessageUtils.send(player, "debugging enabled!");
			else
				MessageUtils.send(player, "debugging disabled!");

			return true;
		}
		
		return false;
	}

	public void doUndo(Player player, Stick stick, String what) {
		int amount = stick.getUndoAmount();
		boolean all = false;

		if (what != "") {
			if (what.equalsIgnoreCase("all"))
				all = true;
			else {
				try {
					amount = Integer.valueOf(what).intValue();
				} catch (NumberFormatException e) {
					MessageUtils.send(player, "Invalid amount, defaulting to " + amount + "!");
				}

				if (amount <= 0)
					return;
			}
		}

		if (all)
			amount = stick.getBlocks().size();

		if (amount == 0) {
			MessageUtils.send(player, "Nothing to undo!");
		} else {
			int blocksRemoved = doUndo(player, stick, amount);

			if (blocksRemoved > 0) {
				if (all)
					MessageUtils.send(player, "All edits in this session were reverted!");
				else
					MessageUtils.send(player, "Last " + blocksRemoved + " edits have been reverted!");

			} else if (blocksRemoved < 0) {
				MessageUtils.send(player, "Access denied!");
			}
		}
	}

	public int doUndo(Player player, Stick stick, int amount) {
		if (!permissionHandler.hasPermission(player, CreativeStickCommand.UNDO.getPermission()))
			return -1;

		Vector<BlockState> blocks = stick.getBlocks();
		int blocksRemoved = 0;
		int lastIndex = blocks.size();
		int currentIndex = lastIndex - 1;

		for (blocksRemoved = 0; blocksRemoved < (lastIndex > amount ? amount : lastIndex); blocksRemoved++) {

			BlockState blockState = blocks.remove(currentIndex--);

			Block block = player.getWorld().getBlockAt(blockState.getX(), blockState.getY(), blockState.getZ());
			block.setTypeId(blockState.getTypeId());
			block.setData(blockState.getRawData());

		}

		blocks.trimToSize();
		return blocksRemoved;
	}

	public boolean toggleReplace(Player player, Stick stick, String item) {
		if (!permissionHandler.hasPermission(player, CreativeStickCommand.REPLACE.getPermission()))
			return false;

		if (!checkOptionalItemSwitch(player, item, stick))
			return false;

		stick.setMode(1);
		sendToggleMessage(player, stick);
		return true;
	}
	
	public boolean toggleBuild(Player player, Stick stick, String item) {
		if (!permissionHandler.hasPermission(player, CreativeStickCommand.BUILD.getPermission()))
			return false;

		if (!checkOptionalItemSwitch(player, item, stick))
			return false;

		if (stick.getItem() == null) {
			MessageUtils.send(player, "Invalid item usage.");
			return false;
		} else {
			stick.setMode(2);
			sendToggleMessage(player, stick);
		}

		return true;
	}

	public boolean toggleMode(Player player, Stick stick, String item) {
		if (!permissionHandler.hasPermission(player, CreativeStickCommand.TOGGLE_MODE.getPermission()))
			return false;

		if (!checkOptionalItemSwitch(player, item, stick))
			return false;

		if (!stick.isEnabled()) {
			stick.toggle();
			sendToggleMessage(player, stick);
			return true;
		} else if (stick.getMode() == Stick.REMOVE_MODE) {
			stick.setMode(Stick.REPLACE_MODE);
		} else if (stick.getMode() == Stick.REPLACE_MODE){
			stick.setMode(Stick.BUILD_MODE);
		} else {
			stick.setMode(Stick.REMOVE_MODE);
		}

		sendToggleMessage(player, stick);
		return true;
	}

	public Stick getStick(Player player) {
		Stick stick;
		if (!sticks.containsKey(player)) {
			stick = new Stick(configLoader);
			sticks.put(player, stick);
			checkForTool(player, stick);
		}

		stick = sticks.get(player);
		return stick;
	}

	public boolean isDebug(Player player) {
		return getStick(player).isDebug();
	}


	private boolean checkOptionalItemSwitch(Player player, String item, Stick stick) {
		if (item != "") {
			List<Material> items = MaterialUtils.getList(item);
			
			// TODO allow all placeable material
			if (items.size() > 1)
				for (Iterator<Material> mI = items.iterator(); mI.hasNext();) {
					Material m = mI.next();
					if (!m.isBlock())
						mI.remove();
				}
			
			if (items.size() != 1) {
				MessageUtils.send(player, "Invalid item specification.");
				String match;
				if (items.size() > 1)
					match = MaterialUtils.getFormattedNameList(items).toString();
				else
					match = "no known items.";

				MessageUtils.send(player, "'" + item + "' matches " + match);
				return false;
			}
			
			stick.setItem(items.get(0));
		}

		return true;
	}

	private String getItemFromArgs(String[] args) {
		if (args.length <= 1) {
			return "";
		}
		
		StringBuilder name = new StringBuilder();
		for (int n = 1; n < args.length; n++) {
			int i = args[n].indexOf(',');
			if (i != -1) {
				
			} else {
				name.append(args[n]);
				name.append(" ");
			}
		}
		
		return name.toString().trim();
	}

	public void sendToggleMessage(Player player, Stick stick) {
		StringBuilder msg = new StringBuilder().append("You are now ").append(ChatColor.RED);
		msg.append(stick.getModeAsString()).append(ChatColor.WHITE);
		if (stick.getMode() != Stick.REMOVE_MODE)
			msg.append(" with ").append(stick.getItemName()).append(".");
		MessageUtils.send(player, msg.toString());
	}

	public synchronized void takeAction(Cancellable event, Player player) {
		if (event.isCancelled()) {
			if (isDebug(player))
				log("cancelled last action of " + player.getDisplayName());
		} else {
			Stick stick = getStick(player);
			BlockState after = stick.dequeue();
			
			if (after == null) {
				return;
			}
			
			if (stick.didItemSwitch()) {
				MessageUtils.send(player, "You are now working with " + MaterialUtils.getFormattedName(stick.getItem()));
			}
			
			
			Block target = after.getBlock();
			BlockState before = target.getState();
			
			// this seems to be necessary for working with water?
			target.setType(Material.AIR);
			target.setData((byte) 0);
			
			target.setType(after.getType());
			target.setData(after.getRawData());
			
			if (stick.isDebug()) {
				log(player.getDisplayName() + " converted a block of " + MaterialUtils.getFormattedName(before.getType()) +
						" to " + MaterialUtils.getFormattedName(after.getType()));
			}
			
			stick.addBlock(before);
			if (!MaterialUtils.isSameMaterial(before.getType(), Material.AIR)) {
				giveItems(player, before);
			}
		}
	}
	
	private void giveItems(Player player, BlockState state) {
		Stick stick = getStick(player);
		
		List<ItemStack> items;
		if (stick.isDrops()) {
			if (stick.doesNaturalDrops()) {
				items = MaterialUtils.getDroppedMaterial(state);
			} else {
				items = new ArrayList<ItemStack>();
				items.add(new ItemStack(state.getTypeId(), 1));
			}
			
			InventoryUtil.giveItems(player, items);
			for (ItemStack is : items) {
				if (isDebug(player)) {
					log(player.getName() + " received " + is.getAmount() + " "
							+ MaterialUtils.getFormattedName(is.getType(), is.getAmount()));
				}
			}
		}
	}
	
	public boolean canUse(Player player, Stick stick) {
		return((player.getItemInHand().getType() == stick.getTool())
				&& stick.isEnabled()
				&& permissionHandler.canUse(player));
	}
}