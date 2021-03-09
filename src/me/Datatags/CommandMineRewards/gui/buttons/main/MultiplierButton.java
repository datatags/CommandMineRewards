package me.Datatags.CommandMineRewards.gui.buttons.main;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.gui.ItemBuilder;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.conversations.MultiplierPrompt;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;

public class MultiplierButton extends GUIButton {
	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		parent.getGUIManager().startConversation(player, new MultiplierPrompt());
	}

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.MULTIPLIER;
	}

	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.MULTIPLIER_MODIFY;
	}

	@Override
	protected ItemBuilder buildBase() {
		return new ItemBuilder(Material.GOLD_INGOT).name(ChatColor.YELLOW + "Multiplier");
	}

	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		return getBase().lore(ChatColor.YELLOW + "Current multiplier: " + gcm.getMultiplier()).build();
	}
}
