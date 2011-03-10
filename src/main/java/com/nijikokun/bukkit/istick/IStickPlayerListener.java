package com.nijikokun.bukkit.istick;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

import com.iminurnetz.bukkit.plugin.util.MessageUtils;
import com.iminurnetz.bukkit.util.MaterialUtils;

public class IStickPlayerListener extends PlayerListener {
	public static IStick plugin;

	public IStickPlayerListener(IStick instance) {
		plugin = instance;
	}

	public void onPlayerAnimation(PlayerAnimationEvent event) {
		int mode = 0;
		Player player = event.getPlayer();
		Stick stick = null;

		stick = plugin.getStick(player);
		mode = stick.getMode();

		if (stick.isUseable()) {

			Location playerLocation = player.getLocation();
			List<Block> targetBlocks = player.getLastTwoTargetBlocks(null, stick.getDistance());
			Block targetedBlock = null;
			Block placedAgainstBlock = null;
			
			Material item = stick.getItem();
			
			if (!item.isBlock()) {
				item = MaterialUtils.getPlaceableMaterial(item);
				if (!item.isBlock()) {
					MessageUtils.send(player, plugin.getMessagePrefix(ChatColor.RED) + " Invalid item usage.");
					return;
				}
			}
			
			Event actionEvent = null;

			if (mode == Stick.REPLACE_MODE) {
				targetedBlock = targetBlocks.get(1);
				placedAgainstBlock = targetBlocks.get(0);

				if (plugin.isDebug(player))
					plugin.log("onPlayerAnimation: Player " + player.getDisplayName() + " replaced a block with " + MaterialUtils.getFormattedName(item));

			} else if (mode == Stick.BUILD_MODE) {
				targetedBlock = targetBlocks.get(0);
				placedAgainstBlock = targetBlocks.get(1);
				
				if (plugin.isDebug(player))
					plugin.log("onPlayerAnimation: Player " + player.getDisplayName() + " placed " + MaterialUtils.getFormattedName(item));
			}
			
			if (mode != Stick.REMOVE_MODE) {
				BlockState state = targetedBlock.getState();
				stick.addBlock(state);

				if (item.equals(Material.FIRE)) {
					targetedBlock.setData((byte) 0);
					actionEvent = new BlockIgniteEvent(targetedBlock, IgniteCause.FLINT_AND_STEEL, player);
				} else {
					actionEvent = new BlockPlaceEvent(Type.BLOCK_PLACED, targetedBlock, null, placedAgainstBlock, new ItemStack(stick.getTool()), player, true);
				}
				
				synchronized (targetedBlock) {
					// needed for replacing blocks with water or fire
					targetedBlock.setTypeId(0);
					targetedBlock.setType(item);
				}

				plugin.getServer().getPluginManager().callEvent(actionEvent);
			}
			
			/* BlockBreakEvent don't seem to work yet
			if (block != null) {
				BlockBreakEvent bbe = new BlockBreakEvent(block, player);
				plugin.getServer().getPluginManager().callEvent(bbe);
				log("fired BlockBreakEvent");
			}
			*/
		}
	}

	public void onPlayerItem(PlayerItemEvent event) {
		Player player = event.getPlayer();
		Stick stick = plugin.getStick(player);

		if (stick.isUseable()) {

			List<Block> targetBlocks = player.getLastTwoTargetBlocks(null, stick.getDistance());
			Block targetedBlock = targetBlocks.get(targetBlocks.size() - 1);
			if (targetedBlock == null) {
				plugin.log("onPlayerItem: block is null... ignored event");
				return;
			}

			if (targetedBlock.getLocation().getBlockY() == 0 && stick.doProtectBottom()) {
				plugin.log(Level.WARNING, "Player " + player.getDisplayName() + " hit rock bottom!");
				return;
			}

			BlockState state = targetedBlock.getState();

			// BlockBreakEvent doesn't seem to do it
			BlockPlaceEvent bpe = new BlockPlaceEvent(Type.BLOCK_PLACED, targetedBlock, null, targetBlocks.get(0), new ItemStack(stick.getTool()), player, true); 

			
			stick.addBlock(state);
			BlockBreakEvent bbe = new BlockBreakEvent(targetedBlock, player);
			plugin.getServer().getPluginManager().callEvent(bbe);
			
			targetedBlock.setTypeId(0);
			if (plugin.isDebug(player)) {
				plugin.log("onPlayerItem: Player " + player.getDisplayName() + " removed a block.");
			}

			plugin.getServer().getPluginManager().callEvent(bpe);

			Material item = targetedBlock.getType();
			item = MaterialUtils.getPlaceableMaterial(item);

			if (item != null) {
				if (item != stick.getItem() && stick.doRightClickSwitch()) {
					stick.setItem(item);
					MessageUtils.send(player, plugin.getMessagePrefix(ChatColor.GREEN) + " You are now working with " + stick.getItemName());
				}
			}

			return;
		}
	}
}
