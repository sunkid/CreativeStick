/**
 * LICENSING
 * 
 * This software is copyright by sunkid <sunkid.com> and is distributed under a dual license:
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
 *    Please contact sunkid.com
 */

package com.iminurnetz.bukkit.plugin.creativestick;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.iminurnetz.bukkit.plugin.util.MessageUtils;
import com.iminurnetz.bukkit.util.MaterialUtils;
import com.nijikokun.bukkit.istick.IStick;
import com.nijikokun.bukkit.istick.Stick;

public class CSEventListener extends BlockListener {

	private IStick plugin;
	
	public CSEventListener(IStick instance) {
		this.plugin = instance;
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		takeAction(event, player);
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		takeAction(event, player);
	}
	
	@Override
	public void onBlockIgnite(BlockIgniteEvent event) {
		Player player = event.getPlayer();
		takeAction(event, player);
	}

	private synchronized void takeAction(Cancellable event, Player player) {
		if (event.isCancelled()) {
			if (plugin.isDebug(player))
				plugin.log("cancelled last action of " + player.getDisplayName());
		} else {
			Stick stick = plugin.getStick(player);
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
				plugin.log(player.getDisplayName() + " converted a block of " + MaterialUtils.getFormattedName(before.getType()) +
						" to " + MaterialUtils.getFormattedName(after.getType()));
			}
			
			stick.addBlock(before);
			if (!MaterialUtils.isSameMaterial(before.getType(), Material.AIR)) {
				giveItems(player, before);
			}
		}
	}
	
	private void giveItems(Player player, BlockState state) {
		Stick stick = plugin.getStick(player);
		
		List<ItemStack> items;
		if (stick.isDrops()) {
			if (stick.doesNaturalDrops()) {
				items = MaterialUtils.getDroppedMaterial(state);
			} else {
				items = new ArrayList<ItemStack>();
				items.add(new ItemStack(state.getTypeId(), 1));
			}
			
			for (ItemStack is : items) {
				if (plugin.isDebug(player)) {
					plugin.log(player.getName() + " received " + is.getAmount() + " "
							+ MaterialUtils.getFormattedName(is.getType(), is.getAmount()));
				}
				if (player.getInventory().firstEmpty() == -1)
					player.getWorld().dropItemNaturally(player.getLocation(), is);
				else {
					player.getInventory().addItem(is);
				}
			}
		}
	}

}
