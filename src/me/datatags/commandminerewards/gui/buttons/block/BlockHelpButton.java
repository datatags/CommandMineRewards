package me.datatags.commandminerewards.gui.buttons.block;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class BlockHelpButton extends GUIButton {

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.BLOCK_MODIFY;
	}

	@Override
	public CMRPermission getClickPermission() {
		return null;
	}

	@Override
	protected ItemBuilder build() {
		return new ItemBuilder(Material.EMERALD_BLOCK).name(ChatColor.GREEN + "Add blocks from your inventory by clicking them!");
	}

	@Override
	public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {}
	
}
