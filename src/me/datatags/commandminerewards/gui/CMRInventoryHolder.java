package me.datatags.commandminerewards.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class CMRInventoryHolder implements InventoryHolder {
	private CMRGUI gui;
	public CMRInventoryHolder(CMRGUI gui) {
		this.gui = gui;
	}
	@Override
	public Inventory getInventory() {
		return null; // if someone's calling this, they probably aren't looking for a GUI
	}
	public CMRGUI getGUI() {
		return gui;
	}
}
