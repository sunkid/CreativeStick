package com.nijikokun.bukkit.istick;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

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

		if ((player.getItemInHand().getType() == stick.getTool()) && (stick.isEnabled())) {
			if (!IStick.permissionHandler.canUse(player)) {
				return;
			}

			Location playerLocation = player.getLocation();
			List<Block> targetBlocks = player.getLastTwoTargetBlocks(null, stick.getDistance());
			Block targetedBlock = targetBlocks.get(targetBlocks.size() - 1);
			
			if (mode == Stick.REPLACE_MODE) {
				Material item = stick.getItem();
				
				BlockPlaceEvent bpe = new BlockPlaceEvent(Type.BLOCK_PLACED, targetedBlock, null, targetBlocks.get(0), new ItemStack(stick.getTool()), player, true); 

				stick.addBlock(targetedBlock.getState());

				synchronized (targetedBlock) {
					targetedBlock.setType(item);
				}

				plugin.getServer().getPluginManager().callEvent(bpe);
				if (plugin.isDebug(player))
					IStick.log(Level.INFO, "fired BlockPlaceEvent");
			} else if (mode == Stick.BUILD_MODE) {
				Material item = stick.getItem();
				
				Block builtBlock = targetBlocks.get(0);
				
				BlockPlaceEvent bpe = new BlockPlaceEvent(Type.BLOCK_PLACED, builtBlock, null, targetedBlock, new ItemStack(stick.getTool()), player, true); 
				
				stick.addBlock(builtBlock.getState());

				synchronized (builtBlock) {
					builtBlock.setType(item);
				}

				plugin.getServer().getPluginManager().callEvent(bpe);
				if (plugin.isDebug(player))
					IStick.log(Level.INFO, "fired BlockPlaceEvent");
			}
			
			/* BlockBreakEvent don't seem to work yet
			if (block != null) {
				BlockBreakEvent bbe = new BlockBreakEvent(block, player);
				plugin.getServer().getPluginManager().callEvent(bbe);
				IStick.log(Level.INFO, "fired BlockBreakEvent");
			}
			*/
		}
	}

	public void onPlayerItem(PlayerItemEvent event) {
		Player player = event.getPlayer();
		Stick stick = plugin.getStick(player);

		if ((player.getItemInHand().getType() == stick.getTool()) && (stick.isEnabled())) {
			if (!IStick.permissionHandler.canUse(player)) {
				return;
			}

			List<Block> targetBlocks = player.getLastTwoTargetBlocks(null, stick.getDistance());
			Block targetedBlock = targetBlocks.get(targetBlocks.size() - 1);

			// BlockBreakEvent doesn't seem to do it
			BlockPlaceEvent bpe = new BlockPlaceEvent(Type.BLOCK_PLACED, targetedBlock, null, targetBlocks.get(0), new ItemStack(stick.getTool()), player, true); 

			if (plugin.isDebug(player))
				IStick.log(Level.INFO, "onPlayerItem: Player " + player.getDisplayName() + " used CS in mode " + stick.getMode());
			
			if (targetedBlock == null) {
				IStick.log(Level.INFO, "onPlayerItem: block is null... ignored event");
				return;
			}

			if (targetedBlock.getLocation().getBlockY() == 0 && stick.doProtectBottom()) {
				IStick.log(Level.WARNING, "Player " + player.getDisplayName() + " hit rock bottom!");
				return;
			}
			
			Material item = targetedBlock.getType();
			stick.addBlock(targetedBlock.getState());

			if (MaterialUtils.isMineCart(item)) {
				// why ignite with fire?
				targetedBlock.setTypeId(51);
				BlockIgniteEvent bie = new BlockIgniteEvent(targetedBlock, BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL, player);
				plugin.getServer().getPluginManager().callEvent(bie);
			} else {
				BlockBreakEvent bbe = new BlockBreakEvent(targetedBlock, player);
				plugin.getServer().getPluginManager().callEvent(bbe);
				if (plugin.isDebug(player))
					IStick.log(Level.INFO, "fired BlockBreakEvent");
				targetedBlock.setTypeId(0);
			}

			plugin.getServer().getPluginManager().callEvent(bpe);
			if (plugin.isDebug(player))
				IStick.log(Level.INFO, "fired BlockPlaceEvent");


			item = MaterialUtils.getPlaceableMaterial(item);

			if (item != null) {
				if (item != stick.getItem() && stick.doRightClickSwitch()) {
					stick.setItem(item);
					Messaging.send(player, Messaging.getMsgFront(ChatColor.GREEN) + " You are now working with " + stick.getItemName());
				}

				if (stick.isDrops()) {
					if (player.getInventory().firstEmpty() == -1)
						player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(item, 1));
					else {
						player.getInventory().addItem(new ItemStack(item, 1));
					}
				}
			}

			return;
		}
	}
}
