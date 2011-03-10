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

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.iminurnetz.bukkit.util.MaterialUtils;
import com.nijikokun.bukkit.istick.IStick;
import com.nijikokun.bukkit.istick.Stick;

public class UndoListener extends BlockListener {

	private IStick plugin;
	
	public UndoListener(IStick instance) {
		this.plugin = instance;
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		doUndo(event, player);
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		doUndo(event, player);
	}
	
	@Override
	public void onBlockIgnite(BlockIgniteEvent event) {
		Player player = event.getPlayer();
		doUndo(event, player);
	}

	private synchronized void doUndo(Cancellable event, Player player) {
		if (event.isCancelled()) {
			plugin.doUndo(player, 1);
			if (plugin.isDebug(player))
				plugin.log("undid last action of " + player.getDisplayName());
		} else {
			giveItems(player);
		}
	}
	
	private void giveItems(Player player) {
		Stick stick = plugin.getStick(player);

		BlockState b = stick.getNewBlockState(); 
		if (b == null) {
			return;
		}
		
		List<ItemStack> items;
		if (stick.isDrops()) {
			if (stick.doesNaturalDrops()) {
				items = MaterialUtils.getDroppedMaterial(b);
			} else {
				items = new ArrayList<ItemStack>();
				items.add(new ItemStack(b.getTypeId(), 1));
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
