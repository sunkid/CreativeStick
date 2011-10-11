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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;

import com.iminurnetz.bukkit.plugin.BukkitPlugin;
import com.iminurnetz.bukkit.plugin.util.MessageUtils;
import com.iminurnetz.bukkit.plugin.util.PluginLogger;
import com.iminurnetz.bukkit.util.InventoryUtil;
import com.iminurnetz.bukkit.util.Item;
import com.iminurnetz.bukkit.util.MaterialUtils;
import com.iminurnetz.util.PersistentProperty;
import com.iminurnetz.util.StringUtils;

public class CSPlugin extends BukkitPlugin {
	public static ArrayList<Player> drop = new ArrayList<Player>();

	protected static PluginLogger logger;
	public static CSPermissionHandler permissionHandler;

	protected static Server server;
	public static PersistentProperty settings;

	private static HashMap<Player, Stick> sticks = new HashMap<Player, Stick>();

	protected CSConfigurationService configLoader;

	private final PlayerListener playerListener = new CSPlayerListener(this);
	
	public int MIN_SERVER_VERSION = 556;
    
    public int getMinimumServerVersion() {
        return this.MIN_SERVER_VERSION;
    }

    public int getMaximumServerVersion() {
        return this.MAX_SERVER_VERSION;
    }

	public CSPlugin() {
		super();
		logger = getLogger();
	}

    public boolean canUse(Player player, Stick stick) {
        return canUse(player, stick, stick.getMode());
    }

    public boolean canUse(Player player, Stick stick, int mode) {
        if (configLoader.useBlockSpawnPermission() && 
                !permissionHandler.canSpawnBlocks(player) &&
                mode != Stick.REMOVE_MODE &&
                !InventoryUtil.hasItem(player, stick.getItem())) {
            return false;
        }
        
        return ((player.getItemInHand().getType() == stick.getTool() || stick.isThrowBuild())
				&& stick.isEnabled()
				&& permissionHandler.canUse(player));
	}

	private void checkForTool(Player player, Stick stick) {
        if (stick.isEnabled() && permissionHandler.canUse(player) && !stick.isThrowBuild()) {
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

	private boolean checkOptionalItemSwitch(Player player, String item, Stick stick) {
		if (StringUtils.isEmpty(item)) {
			return true;
		}
		
		Item i = null;
		
        try {
            i = new Item(item);
            stick.setItem(i);
            return true;
        } catch (InstantiationException e) {
            // ignored
        }

        MessageUtils.send(player, ChatColor.RED, "Invalid item specification.");
        String match;
        List<Material> items = MaterialUtils.getMatchingMaterials(item);
        if (items.size() > 1)
            match = MaterialUtils.getFormattedNameList(items).toString();
        else
            match = "no known items.";

        MessageUtils.send(player, "'" + item + "' matches " + match);
		
		return false;
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

		String value = StringUtils.join(" ", originalArgs, 2);
		
		if (stick.isDebug()) {
			MessageUtils.send(player, "value is " + value);
		}
		
		if (commandEquals(param, "distance")) {
			int distance = stick.getDistance();
			try {
				distance = Integer.valueOf(value).intValue();
			} catch (Exception e) {
				MessageUtils.send(player, ChatColor.RED, "Invalid distance given!");
				return false;
			}

			if (distance != stick.getDistance()) {
				stick.setDistance(distance);
				MessageUtils.send(player, "Distance changed to " + distance + ".");
			}

			return true;
		}

		if (commandEquals(param, "tool")) {
			if (!stick.setTool(value)) {
				MessageUtils.send(player, ChatColor.RED, "Bad tool name or id.");
				return false;
			}
			MessageUtils.send(player, "Your tool has been changed to " + stick.getToolName() + ".");
			return true;
		}

		if (commandEquals(param, "protect-bottom")) {
			boolean protectBottom = StringUtils.isTrue(value);
			stick.doProtectBottom(protectBottom);

			if (protectBottom)
				MessageUtils.send(player, "Your bottom is protected!");
			else
				MessageUtils.send(player, "Your bottom is no longer protected!");

			return true;
		}

		if (commandEquals(param, "natural-drops")) {
			boolean naturalDrops = StringUtils.isTrue(value);
			stick.setNaturalDrops(naturalDrops);

			if (naturalDrops)
				MessageUtils.send(player, "You will now only receive naturally dropped items!");
			else
				MessageUtils.send(player, "You will get what you hit!");

			return true;
		}

		if (commandEquals(param, "right-click-modes")) {
            boolean doRightClickModes = StringUtils.isTrue(value);
            stick.setRightClickModes(doRightClickModes);

            if (doRightClickModes)
                MessageUtils.send(player, "Right-Clicking now uses modes!");
            else
                MessageUtils.send(player, "Right-Clicking no longer uses modes");

            return true;		    
		}
		
		if (commandEquals(param, "right-click-switch")) {
			boolean doRightClickSwitch = StringUtils.isTrue(value);
			stick.setRightClickSwitch(doRightClickSwitch);

			if (doRightClickSwitch)
				MessageUtils.send(player, "Right-Clicking now selects!");
			else
				MessageUtils.send(player, "Right-Clicking no longer selects");

			return true;
		}

		if (commandEquals(param, "undo")) {
			int undo;
			try {
				undo = Integer.valueOf(value).intValue();
			} catch (NumberFormatException e) {
				MessageUtils.send(player, ChatColor.RED, "Invalid number given!");
				return false;
			}

			stick.setUndoAmount(undo);
			MessageUtils.send(player, "Set undos to " + undo);
			return true;
		}

		if (commandEquals(param, "debug-mode")) {
			boolean doDebug = StringUtils.isTrue(value);
			stick.setDebug(doDebug);

			if (doDebug)
				MessageUtils.send(player, "debugging enabled!");
			else
				MessageUtils.send(player, "debugging disabled!");

			return true;
		}
		
		if (commandEquals(param, "announce")) {
			boolean announce = StringUtils.isTrue(value);
			stick.setAnnounce(announce);

			if (announce)
				MessageUtils.send(player, "announcements enabled!");
			else
				MessageUtils.send(player, "announcements disabled!");

			return true;
		}

        if (commandEquals(param, "throw-build")) {
            boolean throwBuild = StringUtils.isTrue(value);
            stick.setThrowBuild(throwBuild);

            if (throwBuild) {
                MessageUtils.send(player, "throw-build mode enabled!");
            } else {
                MessageUtils.send(player, "throw-build mode disabled!");
            }

            return true;
        }

		return false;
	}

	private boolean commandEquals(String param, String string) {
	    if ((param == null && string != null) || (param != null && string == null)) {
	        return false;
	    } else if (param == null) {
	        return true;
	    }
	    
	    String cmd = string.toLowerCase();
	    String abbr = cmd.substring(0, 1);
	    int n = cmd.indexOf('-');
	    while (n != -1) {
	        abbr += cmd.substring(n + 1, n + 2);
	        n = cmd.indexOf('-', n + 1);
	    }
	    
	    return (param.equalsIgnoreCase(cmd) || param.equalsIgnoreCase(abbr));
    }

    public void doIgnore(Player player, Stick stick, String[] originalArgs) {
		if (originalArgs.length == 1) {
			MessageUtils.send(player, ChatColor.RED, "Usage: /cs " + CreativeStickCommand.IGNORE.toString());
			return;
		}
		
		String value = StringUtils.join(" ", originalArgs, 1);

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

            if (configLoader.useBlockSpawnPermission() && !permissionHandler.canSpawnBlocks(player)) {
                InventoryUtil.giveItems(player, new ItemStack(block.getType(), 1, (short) 0, block.getData()));
            }

			block.setTypeId(blockState.getTypeId());
			block.setData(blockState.getRawData());

		}

        player.updateInventory();

		blocks.trimToSize();
		return blocksRemoved;
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
					MessageUtils.send(player, ChatColor.RED, "Invalid amount, defaulting to " + amount + "!");
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
				MessageUtils.send(player, ChatColor.RED, "Access denied!");
			}
		}
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
	
	public Stick getStick(Player player) {
		Stick stick;
		if (!sticks.containsKey(player)) {
			stick = new Stick(configLoader);
			sticks.put(player, stick);
			checkForTool(player, stick);
		}

		stick = sticks.get(player);

        if (stick.isThrowBuild()) {
            stick.setItem(new Item(player.getItemInHand()));
        }

		return stick;
	}

	private void giveItems(Player player, BlockState state) {
		Stick stick = getStick(player);
		
		List<ItemStack> items;
		if (stick.isDrops()) {
			if (stick.doesNaturalDrops()) {
				items = MaterialUtils.getDroppedMaterial(state);
			} else {
				items = new ArrayList<ItemStack>();
				items.add(new ItemStack(state.getTypeId(), 1, (short) 0, state.getRawData()));
			}
			
			InventoryUtil.giveItems(player, items);
			for (ItemStack is : items) {
				if (isDebug(player)) {
					log(player.getName() + " received " + is.getAmount() + " "
							+ MaterialUtils.getFormattedName(is));
				}
			}
		}
	}

	public boolean isDebug(Player player) {
		return getStick(player).isDebug();
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
			if (!stick.isEnabled()) {
				toggle(player, stick);
			}
			success = toggleMode(player, stick, getItemFromArgs(args));
			break;

		case BUILD:
			if (!stick.isEnabled()) {
				toggle(player, stick);
			}
			success = toggleBuild(player, stick, getItemFromArgs(args));
			break;

		case REPLACE:
			if (!stick.isEnabled()) {
				toggle(player, stick);
			}
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
	
	@Override
	public void enablePlugin() {
	    setup();
		registerEvents();
	}

	private void registerEvents() {
		PluginManager pm = server.getPluginManager();

		pm.registerEvent(Event.Type.PLAYER_ANIMATION, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_ITEM_HELD, playerListener, Priority.Monitor, this);
	}

	public void sendToggleMessage(Player player, Stick stick) {
		StringBuilder msg = new StringBuilder().append("You are now ").append(ChatColor.RED);
		msg.append(stick.getModeAsString()).append(ChatColor.WHITE);
		if (stick.getMode() != Stick.REMOVE_MODE)
			msg.append(" with ").append(stick.getItemName()).append(".");
		MessageUtils.send(player, msg.toString());
	}


	public void setup() {
		server = getServer();
		configLoader = new CSConfigurationService(this);
		permissionHandler = configLoader.getPermissionHandler();
	}

	public void takeAction(BlockState before, BlockState after, Player player) {
        Stick stick = getStick(player);
        if (stick.doItemSwitch() && stick.setItem(before.getType(), before.getData())) {
            MessageUtils.send(player, "You are now working with "
                    + MaterialUtils.getFormattedName(stick.getItem()));
        }

        Block target = after.getBlock();

        synchronized (target) {
            // this seems to be necessary for working with water?
            target.setType(Material.AIR);
            target.setData((byte) 0);

            target.setType(after.getType());

            if (configLoader.useBlockSpawnPermission() && target.getType() != Material.AIR && !permissionHandler.canSpawnBlocks(player)) {
                InventoryUtil.remove(player, stick.getItem().getStack());
            }

            MaterialData data = after.getData();
            if (data != null) {
                target.setData(data.getData());
            }
        }
        
        if (stick.isDebug()) {
            log(player.getDisplayName() + " converted a block of "
                    + MaterialUtils.getFormattedName(before.getType()) + " to "
                    + MaterialUtils.getFormattedName(after.getType()));
        }

        stick.addBlock(before);
        if (!MaterialUtils.isSameMaterial(before.getType(), Material.AIR) && player.getGameMode() != GameMode.CREATIVE) {
            giveItems(player, before);
        }
	}

	public void toggle(Player player, Stick stick) {
		stick.toggle();
		MessageUtils.send(player, getName() + " has been " + (stick.isEnabled() ? "enabled" : "disabled") + ".");
		checkForTool(player, stick);
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
	
	public boolean toggleReplace(Player player, Stick stick, String item) {
		if (!permissionHandler.hasPermission(player, CreativeStickCommand.REPLACE.getPermission()))
			return false;

		if (!checkOptionalItemSwitch(player, item, stick))
			return false;

		stick.setMode(1);
		sendToggleMessage(player, stick);
		return true;
	}
}