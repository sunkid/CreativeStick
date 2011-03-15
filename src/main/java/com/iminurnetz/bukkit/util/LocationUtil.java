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
	
	/**
	 * Return the face of a block when viewing the block at location b from location a.
	 * @param player the point one is viewing from
	 * @param targetedBlock the point looked at
	 * @return the BlockFace looked at
	 */
	public static BlockFace getFace(Player player, Block targetedBlock) {
		// TODO fix me please!
		Location playerLoc = player.getEyeLocation();
		double x = targetedBlock.getX() - playerLoc.getX();
		double z = targetedBlock.getZ() - playerLoc.getZ();
		
		if (x == 0 && z == 0) {
			if (targetedBlock.getY() > playerLoc.getY()) {
				return BlockFace.DOWN;
			}
			
			return BlockFace.UP;
		} else if (Math.abs(x) == Math.abs(z)) {
			if (x > 0 && z > 0) {
				return BlockFace.NORTH_EAST;
			} else if (x > 0 && z < 0) {
				return BlockFace.SOUTH_EAST;
			} else if (x < 0 && z > 0) {
				return BlockFace.NORTH_WEST;
			} else if (x < 0 && z < 0) {
				return BlockFace.SOUTH_WEST;
			}
		}
		int direction = (int) Math.floor(Math.atan2(x, z)*2/Math.PI + .5);
		
		System.err.println("x: " + x + " y: " + z + " " + Math.atan2(z, x) + " " + direction);

		switch(direction) {
			case 0:
				return BlockFace.NORTH;
			case 1:
				return BlockFace.EAST;
			case 2:
				return BlockFace.SOUTH;
			case -1:
				return BlockFace.WEST;
		}

		return null;
	}
}
