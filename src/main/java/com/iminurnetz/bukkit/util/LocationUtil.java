package com.iminurnetz.bukkit.util;

import java.util.Formatter;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class LocationUtil {
	public static String getSimpleLocation(Location loc) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("%4d x %4d x %4d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() );
		return sb.toString();
	}

	public static String getSimpleLocation(Block block) {
		return getSimpleLocation(block.getLocation());
	}

	public static boolean isSameLocation(Player player, Block block) {
		return isSameLocation(block, player.getLocation().getBlock(), player.getLocation().getBlock().getRelative(BlockFace.UP));
	}

	/**
	 * Check if any of the Block instances b overlap with a block.
	 * @param block the block to compare to
	 * @param b the set of Block instances to check
	 * @return true if any of the Block instances b overlap with the Block instance block
	 */
	public static boolean isSameLocation(Block block, Block... b) {
		if (block == null) {
			throw new IllegalArgumentException("none of the blocks can be null");
		}
		for (Block bI : b) {
			if (bI == null) {
				throw new IllegalArgumentException("none of the blocks can be null");
			}
			
			if (block.getX() == bI.getX() && block.getY() == bI.getY() && block.getZ() == bI.getZ()) {
				return true;
			}
		}
		
		return false;
	}
}
