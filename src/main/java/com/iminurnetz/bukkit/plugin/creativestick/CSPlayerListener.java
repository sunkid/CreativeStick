package com.iminurnetz.bukkit.plugin.creativestick;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.ChatColor;
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
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

import com.iminurnetz.bukkit.plugin.util.MessageUtils;
import com.iminurnetz.bukkit.util.MaterialUtils;
import com.nijikokun.bukkit.istick.IStick;
import com.nijikokun.bukkit.istick.Stick;

public class CSPlayerListener extends PlayerListener {
	public static IStick plugin;

	public CSPlayerListener(IStick instance) {
		plugin = instance;
	}

    public void onPlayerJoin(PlayerEvent event) {
    	checkStatus(event.getPlayer());
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> checkerHandle = null;

    public void onItemHeldChange(PlayerItemHeldEvent event) {
    	// need to wait for the itemChange to finish
    	final Player p = event.getPlayer();
    	final Runnable checker = new Runnable() {
    		public void run() { checkStatus(p); }
    	};
    	
    	if (checkerHandle != null) {
    		checkerHandle.cancel(true);
    	}
    	
    	checkerHandle = scheduler.schedule(checker, 200, TimeUnit.MILLISECONDS);	
    }

    private void checkStatus(Player player) {
    	Stick stick = plugin.getStick(player);
    	if (stick.isUseable()) {
    		MessageUtils.send(player, ChatColor.GREEN, "You are using a creative stick!");
    	}
    }
    
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		int mode = 0;
		Player player = event.getPlayer();
		Stick stick = null;

		stick = plugin.getStick(player);
		mode = stick.getMode();

		if (stick.isUseable()) {

			List<Block> targetBlocks = player.getLastTwoTargetBlocks(null, stick.getDistance());
			Block targetedBlock = null;
			Block placedAgainstBlock = null;
			
			Material item = stick.getItem();
			
			if (!item.isBlock()) {
				item = MaterialUtils.getPlaceableMaterial(item);
				if (!item.isBlock()) {
					MessageUtils.send(player, ChatColor.RED, "Invalid item usage.");
					return;
				}
			}
			
			Event actionEvent = null;

			if (mode == Stick.REPLACE_MODE || mode == Stick.REMOVE_MODE) {
				targetedBlock = targetBlocks.get(1);
				placedAgainstBlock = targetBlocks.get(0);
			} else if (mode == Stick.BUILD_MODE) {
				targetedBlock = targetBlocks.get(0);
				placedAgainstBlock = targetBlocks.get(1);
			}
			
			BlockState state = targetedBlock.getState();
			state.setType(mode == Stick.REMOVE_MODE ? Material.AIR : item);
			stick.enqueue(state);

			if (MaterialUtils.isSameMaterial(item, Material.FIRE)) {
				state.getData().setData((byte) 0);
				actionEvent = new BlockIgniteEvent(targetedBlock, IgniteCause.FLINT_AND_STEEL, player);
			} else {
				actionEvent = new BlockPlaceEvent(Type.BLOCK_PLACED, targetedBlock, null, placedAgainstBlock, new ItemStack(stick.getTool()), player, true);
			}

			plugin.getServer().getPluginManager().callEvent(actionEvent);
			
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
			state.setType(Material.AIR);
			stick.enqueue(state, true);
			
			// BlockBreakEvent doesn't seem to do it
			BlockPlaceEvent bpe = new BlockPlaceEvent(Type.BLOCK_PLACED, targetedBlock, null, targetBlocks.get(0), new ItemStack(stick.getTool()), player, true); 
			plugin.getServer().getPluginManager().callEvent(bpe);
			
			BlockBreakEvent bbe = new BlockBreakEvent(targetedBlock, player);
			plugin.getServer().getPluginManager().callEvent(bbe);
		}
	}
}
