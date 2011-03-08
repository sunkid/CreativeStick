package com.iminurnetz.bukkit.util;

import java.util.Comparator;

import org.bukkit.Material;

public class ItemsByIdComparator implements Comparator<Material> {

	@Override
	public int compare(Material o1, Material o2) {
		return new Integer(o1.getId()).compareTo(o2.getId());
	}

}
