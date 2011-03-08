package com.iminurnetz.bukkit.creativestick;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.nijikokun.bukkit.istick.IStick;

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
				IStick.log(Level.INFO, "undid last action of " + player.getDisplayName());
		}		
	}
}
