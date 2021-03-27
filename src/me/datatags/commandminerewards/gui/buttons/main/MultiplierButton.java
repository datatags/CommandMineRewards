package me.datatags.commandminerewards.gui.buttons.main;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.conversations.CMRConversationFactory;
import me.datatags.commandminerewards.gui.conversations.MultiplierPrompt;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class MultiplierButton extends GUIButton {
	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		CMRConversationFactory.startConversation(player, new MultiplierPrompt());
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
	protected ItemBuilder build() {
		return new ItemBuilder(Material.GOLD_INGOT).name(ChatColor.YELLOW + "Multiplier")
				.lore(ChatColor.YELLOW + "Current multiplier: " + GlobalConfigManager.getInstance().getMultiplier());
	}
}
