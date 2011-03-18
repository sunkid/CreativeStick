package com.iminurnetz.bukkit.util;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class InventoryUtil {
	public static void giveItems(Player player, List<ItemStack> items) {
		for (ItemStack is : items) {
			giveItems(player, is);
		}
	}

	public static void giveItems(Player player, ItemStack stack) {
		if (player.getInventory().firstEmpty() == -1)
			player.getWorld().dropItemNaturally(player.getLocation(), stack);
		else {
			player.getInventory().addItem(stack);
		}
	}

	public static void giveItems(Player player, Material m) {
		ItemStack stack = new ItemStack(m, 1);
		giveItems(player, stack);
	}

	public static void giveItems(Player player, Material m, int preferredSlot) {
		int slot = player.getInventory().firstEmpty();
		if (slot > 0) {
			if (slot != preferredSlot) {
				switchItems(player, slot, preferredSlot);
			}
			player.getInventory().setItem(preferredSlot, new ItemStack(m, 1));
		} else {
			giveItems(player, m);
		}
	}

	public static void switchItems(Player player, int slot1, int slot2) {
		ItemStack is = player.getInventory().getItem(slot2);
		ItemStack is2 = player.getInventory().getItem(slot1);
		if (is2 != null && is2.getAmount() != 0) {
			player.getInventory().setItem(slot2, is2);
		} else {
			player.getInventory().setItem(slot2, null);
		}
		
		if (is != null && is.getAmount() != 0) {
			player.getInventory().setItem(slot1, is);
		} else {
			player.getInventory().setItem(slot1, null);
		}
	}

	public static void switchToItems(Player player, Material m) {
		if (player.getItemInHand().getType().equals(m)) {
			return;
		}
		
		int slot = player.getInventory().first(m);
		if (slot < 0 || slot == player.getInventory().getHeldItemSlot()) {
			return;
		} else if (slot < 9) {
			// I would really like to just switch to it, but it seems I cannot
		}
		
		switchItems(player, slot, player.getInventory().getHeldItemSlot());
	}
}
