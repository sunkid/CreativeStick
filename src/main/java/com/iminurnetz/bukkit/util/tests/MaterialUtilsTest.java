package com.iminurnetz.bukkit.util.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;

import com.iminurnetz.bukkit.util.MaterialUtils;

import junit.framework.TestCase;

public class MaterialUtilsTest extends TestCase {

	public void testSimpleRetrieval() {
		assertEquals(Material.AIR, MaterialUtils.getMaterialByName("AIR"));
		assertEquals(Material.STONE, MaterialUtils.getMaterialByName("stone"));
		assertEquals(Material.COOKED_FISH, MaterialUtils.getMaterialByName("Cooked Fish"));
		assertEquals(Material.JACK_O_LANTERN, MaterialUtils.getMaterialByName("Jack-o'-Lantern"));
	}
	
	public void testNameFormatting() {
		List <String> materials = MaterialUtils.getFormattedNameList();
		for (String s : materials) {
			Material m = MaterialUtils.getMaterialByName(s);
			assertNotNull(m);
		}
	}
	
	public void testGetMatchingMaterials() {
		List<Material> materials = Arrays.asList(Material.AIR, Material.APPLE, Material.ARROW);
		Collections.sort(materials);
		assertEquals(materials, getSorted(MaterialUtils.getMatchingMaterials("a")));
		materials = Arrays.asList(Material.GOLD_SPADE, Material.GOLD_ORE, Material.GOLD_PICKAXE, Material.GOLD_AXE, Material.GOLD_HOE, Material.GOLD_CHESTPLATE, Material.GOLDEN_APPLE);
		Collections.sort(materials);
		assertEquals(materials, getSorted(MaterialUtils.getMatchingMaterials("Go*e")));	
	}

	public void testIsDamageable() {
		for (int id = 1; id <= 2257; id++) {
			if (!isDamageable(id))
				continue;
			Material m = Material.getMaterial(id);
			if (m != null)
				assertTrue(m.name(), MaterialUtils.isDamageable(m));
		}
	}
	
	public void testIsPlaceable() {
		for (int id = 1; id <= 2257; id++) {
			if (!isStackable(id))
				continue;
			Material m = Material.getMaterial(id);
			if (m != null)
				assertTrue(m.name(), MaterialUtils.isStackable(m));
		}
	}
	
	private List<Material> getSorted(List<Material> list) {
		ArrayList<Material> result = new ArrayList<Material>();
		result.addAll(list);
		Collections.sort(result);
		return result;
	}
	
	private static boolean isDamageable(int id) {
		if ((id >= 256) && (id <= 259)) {
			return true;
		}
		if ((id >= 267) && (id <= 279)) {
			return true;
		}
		if ((id >= 283) && (id <= 286)) {
			return true;
		}
		if ((id >= 290) && (id <= 294)) {
			return true;
		}
		if ((id >= 298) && (id <= 317)) {
			return true;
		}

		return id == 346;
	}

	private static boolean isStackable(int id) {
		if ((id >= 256) && (id <= 261)) {
			return false;
		}
		if ((id >= 267) && (id <= 279)) {
			return false;
		}
		if ((id >= 282) && (id <= 286)) {
			return false;
		}
		if ((id >= 290) && (id <= 294)) {
			return false;
		}
		if ((id >= 297) && (id <= 317)) {
			return false;
		}
		if ((id >= 322) && (id <= 330)) {
			return false;
		}
		if ((id == 319) || (id == 320) || (id == 349) || (id == 350)) {
			return false;
		}
		if ((id == 333) || (id == 335) || (id == 343) || (id == 342)) {
			return false;
		}

		return (id != 354) && (id != 355) && (id != 356) && (id != 2256) && (id != 2257);
	}

}
