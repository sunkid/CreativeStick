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
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.iminurnetz.bukkit.plugin.util.MessageUtils;
import com.iminurnetz.bukkit.util.Item;
import com.iminurnetz.bukkit.util.LocationUtil;
import com.iminurnetz.bukkit.util.MaterialUtils;

public class CSPlayerListener extends PlayerListener {
	public static CSPlugin plugin;

	public CSPlayerListener(CSPlugin instance) {
		plugin = instance;
	}
    
	@Override
    public void onPlayerJoin(PlayerJoinEvent event) {
    	checkStatus(event.getPlayer());
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> checkerHandle = null;

    @Override
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
    	if (plugin.canUse(player, stick) && (stick.doAnnounce() || stick.isDebug())) {
    		MessageUtils.send(player, ChatColor.GREEN, "You are now using " + plugin.getName());
    	}
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        int mode = 0;
		Player player = event.getPlayer();
		Stick stick = null;

		stick = plugin.getStick(player);
		mode = stick.getMode();
		
		boolean isRightClick = false;
		
		if (event.getAction() == Action.RIGHT_CLICK_AIR || 
            event.getAction() == Action.RIGHT_CLICK_BLOCK) {
		    mode = Stick.REMOVE_MODE;
		    isRightClick = true;
		}

		if (plugin.canUse(player, stick)) {
		    
		    event.setCancelled(true);

			List<Block> targetBlocks = player.getLastTwoTargetBlocks(stick.getIgnore(), stick.getDistance());
			
			// sanity check
			if (targetBlocks.size() != 2) {
			    plugin.log(Level.WARNING, "Did not get two blocks to work with");
			    return;
			}
			
			Block targetedBlock = null;
			Block placedAgainstBlock = null;
			
			Item item = stick.getItem();
			
			if (!item.isBlock()) {
				item = MaterialUtils.getPlaceableMaterial(item);
				if (!item.isBlock()) {
					MessageUtils.send(player, ChatColor.RED, "Invalid item usage.");
					return;
				}
			}
			
			BlockEvent actionEvent = null;

			if (mode == Stick.REPLACE_MODE || mode == Stick.REMOVE_MODE) {
				targetedBlock = targetBlocks.get(1);
				placedAgainstBlock = targetBlocks.get(0);
			} else if (mode == Stick.BUILD_MODE) {
				targetedBlock = targetBlocks.get(0);
				placedAgainstBlock = targetBlocks.get(1);
			}

			if (targetedBlock.getLocation().getBlockY() == 0 && stick.doProtectBottom()) {
				plugin.log(Level.WARNING, "Player " + player.getDisplayName() + " hit rock bottom!");
				return;
			}
						
			if (LocationUtil.isSameLocation(player, targetedBlock)) {
				if (stick.isDebug()) {
					MessageUtils.send(player, "** boink **");
				}
				
				return;
			}


			BlockState after = targetedBlock.getState();
            after.setType(mode == Stick.REMOVE_MODE ? Material.AIR : item.getMaterial());

			MaterialData data = null;
			if (mode == Stick.REMOVE_MODE) {
				data = Material.AIR.getNewData((byte) 0);
			} else if (MaterialUtils.isSameMaterial(item.getMaterial(), Material.LADDER)) {
				BlockFace face = LocationUtil.getFace(player, targetedBlock);
				if (stick.isDebug()) {
					MessageUtils.send(player, "clicked " + face + " face! (" + player.getEyeLocation() + ")");
				}
				
				switch (face) {
				case WEST:
					data = Material.LADDER.getNewData((byte) 2);
					break;
				case EAST:
					data = Material.LADDER.getNewData((byte) 3);
					break;
				case SOUTH:
					data = Material.LADDER.getNewData((byte) 4);
					break;
				case NORTH:
					data = Material.LADDER.getNewData((byte) 5);
					break;
				}
			} else {
				data = item.getData();
			}
			
			if (data != null)
				after.setData(data);
			
            BlockState before = targetedBlock.getState();
			if (mode == Stick.REMOVE_MODE) {
			    actionEvent = new BlockBreakEvent(targetedBlock, player);
			} else if (MaterialUtils.isSameMaterial(item.getMaterial(), Material.FIRE)) {
                actionEvent = new BlockIgniteEvent(targetedBlock, IgniteCause.FLINT_AND_STEEL, player);
            } else {
			    actionEvent = new BlockPlaceEvent(after.getBlock(), before, placedAgainstBlock, new ItemStack(stick.getTool()), player, true);
			}
			
			plugin.getServer().getPluginManager().callEvent(actionEvent);
			
			if (!((Cancellable)actionEvent).isCancelled()) {
			    if (isRightClick) {
			        stick.setDoItemSwitch(true);
			    }
			    plugin.takeAction(before, after, player);
			}
		}
	}
    
    @Override
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        final Player player = event.getPlayer();
        final Stick stick = plugin.getStick(player);
        if (plugin.canUse(player, stick)) {
            // try to catch the missing PLAYER_INTERACT
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        return;
                    }
                    
                    long now = new Date().getTime();
    
                    if (now - stick.getLastActionTakenAt() > 40) {
                        Block target = player.getTargetBlock(null, 500);
                        PlayerInteractEvent pie = new PlayerInteractEvent(
                                player,
                                target.getType() == Material.AIR ? Action.LEFT_CLICK_AIR : Action.LEFT_CLICK_BLOCK, 
                                player.getItemInHand(), target,
                                target.getFace(player.getEyeLocation().getBlock()));
                        // plugin.log("Calling onPlayerInteract");
                        onPlayerInteract(pie);
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        }
    }
}
