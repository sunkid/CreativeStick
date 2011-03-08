package com.iminurnetz.bukkit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;

import com.iminurnetz.util.StringUtils;

public class MaterialUtils {
	
	public static List<Material> damageableMaterial = new ArrayList<Material>();
	public static List<Material> damageableItems =
		Arrays.asList(
			Material.IRON_SPADE,
			Material.IRON_PICKAXE,
			Material.IRON_AXE,
			Material.FLINT_AND_STEEL,
			Material.IRON_SWORD,
			Material.WOOD_SWORD,
			Material.WOOD_SPADE,
			Material.WOOD_PICKAXE,
			Material.WOOD_AXE,
			Material.STONE_SWORD,
			Material.STONE_SPADE,
			Material.STONE_PICKAXE,
			Material.STONE_AXE,
			Material.DIAMOND_SWORD,
			Material.DIAMOND_SPADE,
			Material.DIAMOND_PICKAXE,
			Material.DIAMOND_AXE,
			Material.GOLD_SWORD,
			Material.GOLD_SPADE,
			Material.GOLD_PICKAXE,
			Material.GOLD_AXE,
			Material.WOOD_HOE,
			Material.STONE_HOE,
			Material.IRON_HOE,
			Material.DIAMOND_HOE,
			Material.GOLD_HOE,
			Material.LEATHER_HELMET,
			Material.LEATHER_CHESTPLATE,
			Material.LEATHER_LEGGINGS,
			Material.LEATHER_BOOTS,
			Material.LEATHER_HELMET,
			Material.LEATHER_CHESTPLATE,
			Material.LEATHER_LEGGINGS,
			Material.CHAINMAIL_BOOTS,
			Material.CHAINMAIL_HELMET,
			Material.CHAINMAIL_CHESTPLATE,
			Material.CHAINMAIL_LEGGINGS,
			Material.IRON_BOOTS,
			Material.IRON_HELMET,
			Material.IRON_CHESTPLATE,
			Material.IRON_LEGGINGS,
			Material.DIAMOND_BOOTS,
			Material.DIAMOND_HELMET,
			Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_LEGGINGS,
			Material.GOLD_BOOTS,
			Material.GOLD_HELMET,
			Material.GOLD_CHESTPLATE,
			Material.GOLD_LEGGINGS,
			Material.FISHING_ROD,
			Material.CAKE
				);
	
	public static List<Material> stackableMaterial = new ArrayList<Material>();
	public static List<Material> stackableItems =
		Arrays.asList(
			Material.ARROW,
			Material.COAL,
			Material.DIAMOND,
			Material.IRON_INGOT,
			Material.GOLD_INGOT,
			Material.STICK,
			Material.BOWL,
			Material.STRING,
			Material.FEATHER,
			Material.SULPHUR,
			Material.SEEDS,
			Material.WHEAT,
			Material.FLINT,
			Material.PAINTING,
			Material.REDSTONE,
			Material.SNOW_BALL,
			Material.LEATHER,
			Material.CLAY_BRICK,
			Material.CLAY_BALL,
			Material.SUGAR_CANE,
			Material.PAPER,
			Material.BOOK,
			Material.SLIME_BALL,
			Material.EGG,
			Material.COMPASS,
			Material.FISHING_ROD,
			Material.WATCH,
			Material.GLOWSTONE_DUST,
			Material.INK_SACK,
			Material.BONE,
			Material.SUGAR
				);
	
	static {
		for (Material m : Material.values())
			if (m.isBlock())
				stackableMaterial.add(m);
		damageableMaterial.addAll(stackableMaterial);
		damageableMaterial.addAll(damageableItems);
		stackableMaterial.addAll(stackableItems);
	}
	
	public static Material getMaterialByName(String name) {
		return Material.matchMaterial(name.replaceAll("-", " "));
	}

	public static List<String> getMatchingMaterialNames(String string) {
		ArrayList<String> matches = new ArrayList<String>();
		for (String s : StringUtils.closestMatch(string, getMaterialNames())) {
			matches.add(s);
		}
		return matches;
	}

	public static List<Material> getMatchingMaterials(String string) {
		ArrayList<Material> matches = new ArrayList<Material>();
		for (String s : StringUtils.closestMatch(string, getFormattedNameList())) {
			matches.add(getMaterialByName(s));
		}
		return matches;
	}

	public static List<Material> getListById() {
		List<Material> materials = getList();
		Collections.sort(materials, new ItemsByIdComparator());
		return materials;
	}

	public static List<String> getMaterialNames() {
		return getMaterialNames(Arrays.asList(Material.values()));
	}

	public static List<String> getMaterialNames(List<Material> materials) {
		ArrayList<String> names = new ArrayList<String>();

		for (Material m : materials) {
			names.add(m.name());
		}

		return names;
	}

	public static List<String> getFormattedNameList() {
		return getFormattedNameList(getList());
	}

	public static List<String> getFormattedNameList(List<Material> materials) {
		ArrayList<String> names = new ArrayList<String>();
		for (Material m : materials) {
			names.add(getFormattedName(m));
		}
		return names;
	}

	public static List<String> getNamesById() {
		return getMaterialNames(getListById());
	}

	public static List<Material> getList() {
		return Arrays.asList(Material.values());
	}

	public static boolean isWater(Material m) {
		return m.equals(Material.WATER) || m.equals(Material.STATIONARY_WATER);
	}

	public static boolean isLava(Material m) {
		return m.equals(Material.LAVA) || m.equals(Material.STATIONARY_LAVA);
	}

	public static boolean isWoodenDoor(Material m) {
		return m.equals(Material.WOOD_DOOR) || m.equals(Material.WOODEN_DOOR);
	}

	public static boolean isIronDoor(Material m) {
		return m.equals(Material.IRON_DOOR) || m.equals(Material.IRON_DOOR_BLOCK);
	}

	public static boolean isMineCart(Material m) {
		return m.equals(Material.STORAGE_MINECART) || m.equals(Material.POWERED_MINECART) || m.equals(Material.MINECART);
	}

	public static boolean isSign(Material m) {
		return m.equals(Material.SIGN_POST) || m.equals(Material.WALL_SIGN) || m.equals(Material.SIGN);
	}

	public static boolean isWater(int id) {
		return isWater(Material.getMaterial(id));
	}

	public static boolean isLava(int id) {
		return isLava(Material.getMaterial(id));
	}

	public static boolean isWoodenDoor(int id) {
		return isWoodenDoor(Material.getMaterial(id));
	}

	public static boolean isIronDoor(int id) {
		return isIronDoor(Material.getMaterial(id));
	}

	public static boolean isMineCart(int id) {
		return isMineCart(Material.getMaterial(id));
	}

	public static boolean isSign(int id) {
		return isSign(Material.getMaterial(id));
	}

	public static Material getPlaceableMaterial(int id) {
		return getPlaceableMaterial(Material.getMaterial(id));
	}

	public static Material getPlaceableMaterial(Material m) {
		if (isWater(m)) {
			return Material.WATER;
		}

		if (isLava(m)) {
			return Material.LAVA;
		}

		if (isWoodenDoor(m)) {
			return Material.WOOD_DOOR;
		}

		if (isIronDoor(m)) {
			return Material.IRON_DOOR;
		}

		if (isSign(m)) {
			return Material.SIGN;
		}

		return m;
	}

	public static List<Material> getList(String item) {
		int id;
		List<Material> results = new ArrayList<Material>();
		Material m;
		try {
			id = Integer.valueOf(item).intValue();
			m = Material.getMaterial(id);
		} catch (NumberFormatException e) {
			m = Material.getMaterial(item);
			if (m == null) {
				results = getMatchingMaterials(item);
			}
		}

		if (m != null)
			results.add(m);

		return results;
	}

	public static Material getMaterial(String item) {
		List<Material> materials = getList(item);
		if (materials.size() != 1)
			return null;
		else
			return materials.get(0);
	}

	public static String getFormattedName(Material m) {
		return StringUtils.constantCaseToEnglish(m.name());
	}

	public static boolean isStackable(int id) {
		return isStackable(Material.getMaterial(id));
	}

	public static boolean isStackable(Material m) {
		return stackableMaterial.contains(m);
	}

	public static boolean isDamageable(int id) {
		return isDamageable(Material.getMaterial(id));
	}
	
	public static boolean isDamageable(Material m) {
		return damageableMaterial.contains(m);
	}

	// original code from Nijikokun's Items class
	public static boolean validateType(int id, int type) {
		if ((type == -1) || (type == 0)) {
			return true;
		}

		if (((id == 35) || (id == 351) || (id == 63)) && (type >= 0)
				&& (type <= 15)) {
			return true;
		}

		if ((id == 17) && (type >= 0) && (type <= 2)) {
			return true;
		}

		if (((id == 91) || (id == 86) || (id == 67) || (id == 53) || (id == 77)
				|| (id == 71) || (id == 64))
				&& (type >= 0) && (type <= 3)) {
			return true;
		}

		if ((id == 66) && (type >= 0) && (type <= 9)) {
			return true;
		}

		if ((id == 68) && (type >= 2) && (type <= 5)) {
			return true;
		}

		if ((id == 263) && ((type == 0) || (type == 1))) {
			return true;
		}

		return isDamageable(id);
	}

}
