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

package com.iminurnetz.bukkit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

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

	public static Map<Material, String> pluralWords = new HashMap<Material, String>();
	static {
		for (Material m : Material.values()) {
			if (m.name().endsWith("S")) {
				pluralWords.put(m, getFormattedName(m));
			} else if (isOre(m)) {
				pluralWords.put(m, "blocks of " + getFormattedName(m));
			} else {
				pluralWords.put(m, getFormattedName(m) + "s");
			}
		}
		
		// leave as is
		for (Material m : Arrays.asList(
				Material.AIR,
				Material.WATER,
				Material.STATIONARY_WATER,
				Material.LAVA,
				Material.STATIONARY_LAVA,
				Material.CROPS,
				Material.COOKED_FISH,
				Material.RAW_FISH
			)) {
			pluralWords.put(m, getFormattedName(m));
		}
		
		for (Material m : Arrays.asList(
				Material.DIRT,
				Material.WOOD,
				Material.BEDROCK,
				Material.SAND,
				Material.GLASS,
				Material.WOOL,
				Material.TNT,
				Material.GRAVEL,
				Material.OBSIDIAN,
				Material.SOIL,
				Material.CLAY,
				Material.NETHERRACK,
				Material.SNOW,
				Material.ICE,
				Material.SOUL_SAND,
				Material.SULPHUR,
				Material.GLOWSTONE_DUST
			)) {
			pluralWords.put(m, "blocks of " + getFormattedName(m));
		}
		
		for (Material m : Arrays.asList(
				Material.TORCH,
				Material.WORKBENCH,
				Material.JUKEBOX,
				Material.COMPASS,
				Material.WATCH)) {
			pluralWords.put(m, getFormattedName(m) + "es");
		}
		
		for (Material m : Arrays.asList(
				Material.PORK,
				Material.GRILLED_PORK,
				Material.PAPER,
				Material.FLINT_AND_STEEL,
				Material.LEATHER)) {
			pluralWords.put(m, "pieces of " + getFormattedName(m));
		}		
		
		pluralWords.put(Material.BOOKSHELF, "bookshelves");
		pluralWords.put(Material.SUGAR, "sugar cubes");
		pluralWords.put(Material.CACTUS, "cacti");
		pluralWords.put(Material.REDSTONE_TORCH_OFF, "redstone torches (off)");
		pluralWords.put(Material.REDSTONE_TORCH_ON, "redstone torches (on)");
		pluralWords.put(Material.DIODE_BLOCK_OFF, "diode blocks (off)");
		pluralWords.put(Material.DIODE_BLOCK_ON, "diode blocks (on)");
	}
	
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

	/**
	 * Check if the first material is "the same" as any of the other materials by comparing
	 * their names only.
	 * @param m1 the Material to check
	 * @param m2 the Material enum members to check against
	 * @return true if the name of any of the m2 Material enum members matches the name of m1
	 */
	public static boolean isSameMaterial(Material m1, Material... m2) {
		for (Material m : m2) {
			if (m1.name().equals(m)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isWater(Material m) {
		return isSameMaterial(m, Material.WATER, Material.STATIONARY_WATER);
	}

	public static boolean isLava(Material m) {
		return isSameMaterial(m, Material.LAVA, Material.STATIONARY_LAVA);
	}

	public static boolean isWoodenDoor(Material m) {
		return isSameMaterial(m, Material.WOOD_DOOR, Material.WOODEN_DOOR);
	}

	public static boolean isIronDoor(Material m) {
		return isSameMaterial(m, Material.IRON_DOOR, Material.IRON_DOOR_BLOCK);
	}

	public static boolean isMineCart(Material m) {
		return isSameMaterial(m, Material.STORAGE_MINECART, Material.POWERED_MINECART, Material.MINECART);
	}

	public static boolean isSign(Material m) {
		return isSameMaterial(m, Material.SIGN_POST, Material.WALL_SIGN, Material.SIGN);
	}
	
	public static boolean isBed(Material m) {
		return isSameMaterial(m, Material.BED, Material.BED_BLOCK);
	}
	
	public static boolean isDiode(Material m) {
		return isSameMaterial(m, Material.DIODE, Material.DIODE_BLOCK_ON, Material.DIODE_BLOCK_OFF);
	}
	
	public static boolean isSugar(Material m) {
		return isSameMaterial(m, Material.SUGAR, Material.SUGAR_CANE, Material.SUGAR_CANE_BLOCK);
	}
	
	public static boolean isCoal(Material m) {
		return isSameMaterial(m, Material.COAL, Material.COAL_ORE);
	}
	
	public static boolean isGold(Material m) {
		return isSameMaterial(m, Material.GOLD_BLOCK, Material.GOLD_INGOT, Material.GOLD_ORE);
	}
	
	public static boolean isIron(Material m) {
		return isSameMaterial(m, Material.IRON_BLOCK, Material.IRON_INGOT, Material.IRON_ORE);
	}
	
	public static boolean isDiamond(Material m) {
		return isSameMaterial(m, Material.DIAMOND, Material.DIAMOND_BLOCK, Material.DIAMOND_ORE);
	}
	
	public static boolean isRedstone(Material m) {
		return isSameMaterial(m, Material.REDSTONE, Material.REDSTONE_ORE, Material.GLOWING_REDSTONE_ORE);
	}

	public static boolean isSnow(Material m) {
		return isSameMaterial(m, Material.SNOW_BALL, Material.SNOW_BLOCK, Material.SNOW);
	}
	
	public static boolean isClay(Material m) {
		return isSameMaterial(m, Material.CLAY, Material.CLAY_BALL);		
	}
	
	public static boolean isBrick(Material m) {
		return isSameMaterial(m, Material.CLAY_BRICK, Material.BRICK);
	}
	
	public static boolean isGlowstone(Material m) {
		return isSameMaterial(m, Material.GLOWSTONE, Material.GLOWSTONE_DUST);
	}
	
	public static boolean isDirt(Material m) {
		return isSameMaterial(m, Material.GRASS, Material.DIRT, Material.SOIL);
	}
	
	public static boolean isFurnace(Material m) {
		return isSameMaterial(m, Material.FURNACE, Material.BURNING_FURNACE);
	}
	
	public static boolean isDoor(Material m) {
		return isSameMaterial(m, Material.WOODEN_DOOR, Material.IRON_DOOR, Material.WOOD_DOOR, Material.IRON_DOOR_BLOCK);
	}
	
	public static boolean isOre(Material m) {
		return m.name().endsWith("_ORE");
	}
	
	public static boolean isBlockBlock(Material m) {
		return m.name().endsWith("_BLOCK");
	}
	
	public static boolean isStep(Material m) {
		return m.name().endsWith("STEP");
	}
	
	/**
	 * Retrieves the items dropped when mining a block with this particular state.
	 * @param state the state of the Block that was mined
	 * @return a list of ItemStacks containing all items dropped
	 */
	public static List<ItemStack> getDroppedMaterial(BlockState state) {
		Material m = state.getType();
		byte originalData = state.getRawData();
		
		int n = 0;
		Material dm = Material.AIR;
		byte data = (byte) 0;

		List<ItemStack> items = new ArrayList<ItemStack>();
		
		Random generator = new Random();			

		if (isSameMaterial(m, Material.STONE, Material.COBBLESTONE)) {
			dm = Material.COBBLESTONE;
		} else if (isOre(m)) {
			if (isSameMaterial(m, Material.LAPIS_ORE)) {
				n = generator.nextInt(5) + 4;
				dm = Material.INK_SACK;
				data = (byte) 4;
			} else if (isRedstone(m)) {
				n = generator.nextInt(2) + 4;
				dm = Material.REDSTONE;
			} else if (isSameMaterial(m, Material.COAL_ORE, Material.DIAMOND_ORE)) {
				dm = MaterialUtils.getMaterial(m.name().substring(0, m.name().length() - 4));
			} else {
				n = 1;
				dm = m;
			}
		} else if (isDirt(m)) {
			n = 1;
			dm = Material.DIRT;
		} else if (isBlockBlock(m) && !isBed(m)) {
			n = 1;
			if (isSameMaterial(m, Material.SNOW_BLOCK)) {
				n = 4;
				dm = Material.SNOW_BALL;
			} else if (isSameMaterial(m, Material.SUGAR_CANE_BLOCK)) {
				dm = Material.SUGAR_CANE; 
			} else {
				dm = m;
			}
		} else if (isBed(m)) {
			dm = Material.BED_BLOCK;
			n = 1;
		} else if (isStep(m)) {
			dm = Material.STEP;
			n = isSameMaterial(m, Material.STEP) ? 1 : 2;
		} else if (isSameMaterial(m, Material.REDSTONE_WIRE)) {
			dm = Material.REDSTONE;
			n = 1;
		} else if (isSameMaterial(m, Material.CROPS) && originalData == (byte) 7) {
			n = generator.nextInt(4);
			items.add(new ItemStack(Material.SEEDS, n));
			
			dm = Material.WHEAT;
			n = 1;
		} else if (isFurnace(m)) {
			dm = Material.FURNACE;
			n = 1;
		} else if (isSign(m)) {
			dm = Material.SIGN;
			n = 1;
		} else if (isDoor(m)) {
			dm = isSameMaterial(Material.WOODEN_DOOR) ? Material.WOOD_DOOR : Material.IRON_DOOR;
			n = 1;
		} else if (isSameMaterial(Material.REDSTONE_TORCH_OFF)) {
			dm = Material.REDSTONE_TORCH_ON;
			n = 1;
		} else if (isSameMaterial(Material.SNOW)) {
			dm = Material.SNOW_BALL;
			n = 1;
		} else if (isDiode(m)) {
			dm = Material.DIODE;
			n = 1;
		}

		if (n != 0) {
			items.add(new ItemStack(dm, n, new Byte(data)));
		}
		
		return items;
	}

	public static Material getPlaceableMaterial(int id) {
		return getPlaceableMaterial(Material.getMaterial(id));
	}

	public static Material getPlaceableMaterial(Material m) {
		if (m.isBlock())
			return m;
		
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
		
		if (isSameMaterial(m, Material.FLINT_AND_STEEL)) {
			return Material.FIRE;
		}
		
		if (isBed(m)) {
			return Material.BED_BLOCK;
		}
		
		if (isDiode(m)) {
			return Material.DIODE_BLOCK_OFF;
		}
		
		if (isSugar(m)) {
			return Material.SUGAR_CANE_BLOCK;
		}
		
		if (isCoal(m)) {
			return Material.COAL_ORE;
		}
		
		if (isDiamond(m)) {
			return Material.DIAMOND_ORE;
		}
		
		if (isGold(m)) {
			return Material.GOLD_ORE;
		}
		
		if (isIron(m)) {
			return Material.IRON_ORE;
		}
		
		if (isRedstone(m)) {
			return Material.REDSTONE_ORE;
		}
		
		if (isGlowstone(m)) {
			return Material.GLOWSTONE;
		}
		
		if (isSnow(m)) {
			return Material.SNOW;
		}
		
		if (isClay(m)) {
			return Material.CLAY;
		}
		
		if (isBrick(m)) {
			return Material.BRICK;
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
		String result = StringUtils.constantCaseToEnglish(m.name());
		result.replaceAll("(on|off)$", "($1)");
		return result;
	}

	public static String getFormattedName(Material m, int amount) {
		if (amount > 1) {
			return pluralWords.get(m);
		}
		
		return getFormattedName(m);
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

	public static Material getPlaceableMaterial(String item) {
		Material m = getMaterial(item);
		// TODO other stuff can be placed too, eg. cake
		if (m.isBlock())
			return m;
		return null;
	}

}
