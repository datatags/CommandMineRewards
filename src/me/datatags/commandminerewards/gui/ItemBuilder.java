package me.datatags.commandminerewards.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder implements Cloneable {
	private Material material;
	private String name;
	private List<String> lore = new ArrayList<String>();
	private ItemMeta im;
	public ItemBuilder(Material material) {
		this.material = material;
		this.im = new ItemStack(material).getItemMeta();
	}
	public ItemBuilder(Material material, String name, List<String> lore, ItemMeta im) {
		this.material = material;
		this.name = name;
		this.lore = lore;
		this.im = im;
	}
	public ItemBuilder name(String name) {
		this.name = name;
		return this;
	}
	public ItemBuilder lore(String lore) {
		this.lore.add(lore);
		return this;
	}
	public List<String> getLore() {
		return lore;
	}
	public ItemMeta getItemMeta() {
		return im;
	}
	public Material getType() {
		return material;
	}
	public boolean hasName() {
		return name != null;
	}
	public ItemStack build() {
		ItemStack is = new ItemStack(material);
		im.setDisplayName(name);
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	@Override
	public ItemBuilder clone() {
		return new ItemBuilder(material, name, new ArrayList<String>(lore), im.clone());
	}
}
