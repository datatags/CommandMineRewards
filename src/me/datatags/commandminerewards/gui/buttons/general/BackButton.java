package me.datatags.commandminerewards.gui.buttons.general;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class BackButton extends GUIButton {

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.GUI;
	}

	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.GUI;
	}

	@Override
	protected ItemBuilder buildBase() {
		return new ItemBuilder(Material.BARRIER).name(ChatColor.RED + "Back");
	}

	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		return getBase().build();
	}

	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		parent.getPreviousGUI().openFor(player);
	}

}
