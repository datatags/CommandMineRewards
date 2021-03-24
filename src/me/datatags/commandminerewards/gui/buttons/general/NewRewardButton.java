package me.datatags.commandminerewards.gui.buttons.general;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.conversations.RewardNamePrompt;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class NewRewardButton extends GUIButton {
	private RewardGroup group;
	public NewRewardButton(RewardGroup group) {
		this.group = group;
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.REWARD_MODIFY;
	}

	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.REWARD_MODIFY;
	}

	@Override
	protected ItemBuilder buildBase() {
		return new ItemBuilder(Material.EMERALD_BLOCK).name(ChatColor.GREEN + "New reward" + (group == null ? " group" : ""));
	}

	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		return getBase().build();
	}

	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		parent.getGUIManager().startConversation(player, new RewardNamePrompt(group));
	}

}
