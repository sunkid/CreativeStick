package com.nijikokun.bukkit.istick;

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
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;

import com.iminurnetz.bukkit.plugin.BukkitPlugin;
import com.iminurnetz.bukkit.plugin.creativestick.CSPermissionHandler;
import com.iminurnetz.bukkit.plugin.creativestick.ConfigurationService;
import com.iminurnetz.bukkit.plugin.creativestick.CreativeStickCommand;
import com.iminurnetz.bukkit.plugin.creativestick.UndoListener;
import com.iminurnetz.bukkit.plugin.util.MessageUtils;
import com.iminurnetz.bukkit.plugin.util.PluginLogger;
import com.iminurnetz.bukkit.util.MaterialUtils;
import com.iminurnetz.util.PersistentProperty;

public class IStick extends BukkitPlugin {
	protected static PluginLogger logger;

	private final PlayerListener playerListener = new IStickPlayerListener(this);
	private final BlockListener undoListener = new UndoListener(this);

	public static CSPermissionHandler permissionHandler;
	protected ConfigurationService configLoader;

	private static HashMap<Player, Stick> sticks = new HashMap<Player, Stick>();

	public static PersistentProperty settings;

	public static ArrayList<Player> drop = new ArrayList<Player>();
	protected static Server server;

	public IStick() {
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
			MessageUtils.send(player, getMessagePrefix(ChatColor.GREEN) + " Drops have been " + (stick.isDrops() ? "enabled" : "disabled") + ".");
			break;

		case TOGGLE:
			stick.toggle();
			MessageUtils.send(player, getMessagePrefix(ChatColor.GREEN) + " Has been " + (stick.isEnabled() ? "enabled" : "disabled") + ".");
			break;

		case TOGGLE_MODE:
			success = toggleMode(player, args.length > 1 ? args[1] : "");
			break;

		case BUILD:
			success = toggleBuild(player, args.length > 1 ? args[1] : "");
			break;

		case REPLACE:
			success = toggleReplace(player, args.length > 1 ? args[1] : "");
			break;

		case UNDO:
			doUndo(player, args.length > 1 ? args[1] : "");
			break;

		case CONFIG:
			success = doConfig(player, args);
			break;

		default:
			success = false;
		}

		if (!success)
			MessageUtils.send(player, ChatColor.RED + "Usage: /" + CreativeStickCommand.CREATIVE_STICK_COMMAND + " " + cmd);

		return true;
	}

	public boolean doConfig(Player player, String[] args) {
		Stick stick = getStick(player);

		if (args.length < 2) {
			MessageUtils.send(player, stick.showSettings());
			return true;
		}

		if (!permissionHandler.hasPermission(player, CSPermissionHandler.CAN_CONFIGURE_PERMISSION)) {
			MessageUtils.send(player, CreativeStickCommand.CONFIG.showAccessDenied());
			return true;
		}

		String param = args[1];
		String value = args.length < 3 ? "" : args[2];

		if (param.equalsIgnoreCase("distance")) {
			int distance = stick.getDistance();
			try {
				distance = Integer.valueOf(value).intValue();
			} catch (NumberFormatException e) {
				MessageUtils.send(player, getMessagePrefix(ChatColor.RED) + " Invalid distance given!");
				return false;
			}

			if (distance != stick.getDistance()) {
				stick.setDistance(distance);
				MessageUtils.send(player, getMessagePrefix(ChatColor.GREEN) + " Distance changed to " + distance + ".");
			}

			return true;
		}

		if (param.equalsIgnoreCase("tool")) {
			if (!stick.setTool(value)) {
				MessageUtils.send(player, getMessagePrefix(ChatColor.RED) + " Bad tool name or id.");
				return false;
			}
			MessageUtils.send(player, getMessagePrefix(ChatColor.GREEN) + " Your tool has been changed to " + stick.getToolName() + ".");
			return true;
		}

		if (param.equalsIgnoreCase("protect-bottom")) {
			boolean protectBottom = new Boolean(value).booleanValue();
			stick.doProtectBottom(protectBottom);

			if (protectBottom)
				MessageUtils.send(player, getMessagePrefix(ChatColor.GREEN) + " Your bottom is protected!");
			else
				MessageUtils.send(player, getMessagePrefix(ChatColor.GREEN) + " Your bottom is no longer protected!");

			return true;
		}

		if (param.equalsIgnoreCase("natural-drops")) {
			boolean naturalDrops = new Boolean(value).booleanValue();
			stick.setNaturalDrops(naturalDrops);

			if (naturalDrops)
				MessageUtils.send(player, getMessagePrefix(ChatColor.GREEN) + " You will now only receive naturally dropped items!");
			else
				MessageUtils.send(player, getMessagePrefix(ChatColor.GREEN) + " You will get what you hit!");

			return true;
		}

		if (param.equalsIgnoreCase("right-click-switch")) {
			boolean doRightClickSwitch = new Boolean(value).booleanValue();
			stick.setRightClickSwitch(doRightClickSwitch);

			if (doRightClickSwitch)
				MessageUtils.send(player, getMessagePrefix(ChatColor.GREEN) + " Right-Clicking now selects!");
			else
				MessageUtils.send(player, getMessagePrefix(ChatColor.GREEN) + " Right-Clicking no longer selects");

			return true;
		}

		if (param.equalsIgnoreCase("undo")) {
			int undo;
			try {
				undo = Integer.valueOf(value).intValue();
			} catch (NumberFormatException e) {
				MessageUtils.send(player, getMessagePrefix(ChatColor.RED) + " Invalid number given!");
				return false;
			}

			stick.setUndoAmount(undo);
			return true;
		}

		if (param.equalsIgnoreCase("debug")) {
			boolean doDebug = new Boolean(value).booleanValue();
			stick.setDebug(doDebug);

			if (doDebug)
				MessageUtils.send(player, getMessagePrefix(ChatColor.GREEN) + " debugging enabled!");
			else
				MessageUtils.send(player, getMessagePrefix(ChatColor.GREEN) + " debugging disabled!");

			return true;
		}

		return false;
	}

	public void doUndo(Player player, String what) {
		Stick stick = getStick(player);
		int amount = stick.getUndoAmount();
		boolean all = false;

		if (what != "") {
			if (what.equalsIgnoreCase("all"))
				all = true;
			else {
				try {
					amount = Integer.valueOf(what).intValue();
				} catch (NumberFormatException e) {
					MessageUtils.send(player, getMessagePrefix(ChatColor.RED) + " Invalid amount, defaulting to " + amount + "!");
				}

				if (amount <= 0)
					return;
			}
		}

		if (all)
			amount = stick.getBlocks().size();

		if (amount == 0) {
			MessageUtils.send(player, getMessagePrefix(ChatColor.RED) + " Nothing to undo!");
		} else {
			int blocksRemoved = doUndo(player, amount);

			if (blocksRemoved > 0) {
				if (all)
					MessageUtils.send(player, getMessagePrefix(ChatColor.RED) + " All edits in this session were reverted!");
				else
					MessageUtils.send(player, getMessagePrefix(ChatColor.RED) + " Last " + blocksRemoved + " edits have been reverted!");

			} else if (blocksRemoved < 0) {
				MessageUtils.send(player, getMessagePrefix(ChatColor.RED) + " Access denied!");
			}
		}
	}

	public int doUndo(Player player, int amount) {
		if (!permissionHandler.hasPermission(player, CreativeStickCommand.UNDO.getPermission()))
			return -1;

		Stick stick = getStick(player);
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

	public boolean toggleReplace(Player player, String item) {
		if (!permissionHandler.hasPermission(player, CreativeStickCommand.REPLACE.getPermission()))
			return false;

		Stick stick = getStick(player);

		if (!checkOptionalItemSwitch(player, item, stick))
			return false;

		stick.setMode(1);
		sendToggleMessage(player, stick);
		return true;
	}
	
	public boolean toggleBuild(Player player, String item) {
		if (!permissionHandler.hasPermission(player, CreativeStickCommand.BUILD.getPermission()))
			return false;

		Stick stick = getStick(player);

		if (!checkOptionalItemSwitch(player, item, stick))
			return false;

		if (stick.getItem() == null) {
			MessageUtils.send(player, getMessagePrefix(ChatColor.RED) + " Invalid item usage.");
			return false;
		} else {
			stick.setMode(2);
			sendToggleMessage(player, stick);
		}

		return true;
	}

	public boolean toggleMode(Player player, String item) {
		if (!permissionHandler.hasPermission(player, CreativeStickCommand.TOGGLE_MODE.getPermission()))
			return false;

		Stick stick = getStick(player);
		if (!checkOptionalItemSwitch(player, item, stick))
			return false;

		if (!stick.isEnabled()) {
			stick.toggle();
			sendToggleMessage(player, stick);
			return true;
		} else if (stick.getMode() == 1) {
			stick.setMode(2);
		} else {
			stick.setMode(1);
		}

		sendToggleMessage(player, stick);
		return true;
	}

	public Stick getStick(Player player) {
		Stick stick;
		if (!sticks.containsKey(player)) {
			stick = new Stick(configLoader);
			sticks.put(player, stick);
		}

		stick = sticks.get(player);
		stick.setUseable((player.getItemInHand().getType() == stick.getTool())
							&& stick.isEnabled()
							&& permissionHandler.canUse(player));
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
				MessageUtils.send(player, getMessagePrefix(ChatColor.RED) + " Invalid item specification.");
				String match;
				if (items.size() > 1)
					match = MaterialUtils.getFormattedNameList(items).toString();
				else
					match = "no known items.";

				MessageUtils.send(player, getMessagePrefix(ChatColor.RED) + "'" + item + "' matches " + match);
				return false;
			}
			
			stick.setItem(items.get(0));
		}

		return true;
	}

	private void sendToggleMessage(Player player, Stick stick) {
		StringBuilder msg = new StringBuilder(getMessagePrefix(ChatColor.GREEN)).append(" You are now ").append(ChatColor.RED);
		msg.append(stick.getModeAsString()).append(ChatColor.WHITE);
		if (stick.getMode() != Stick.REMOVE_MODE)
			msg.append(" with ").append(stick.getItemName()).append(".");
		MessageUtils.send(player, msg.toString());
	}

}