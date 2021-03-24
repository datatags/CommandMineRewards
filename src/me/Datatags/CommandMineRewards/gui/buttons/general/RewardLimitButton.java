package me.Datatags.CommandMineRewards.gui.buttons.general;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.RewardGroup;
import me.Datatags.CommandMineRewards.gui.ItemBuilder;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.conversations.RewardLimitPrompt;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;

public class RewardLimitButton extends GUIButton {
	public RewardGroup group;
	public RewardLimitButton(RewardGroup group) {
		this.group = group;
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.LIMIT;
	}

	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.LIMIT_MODIFY;
	}

	@Override
	protected ItemBuilder buildBase() {
		return new ItemBuilder(Material.CLOCK).name(ChatColor.RED + "Reward Limit");
	}

	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		int value;
		if (group == null) {
			value = gcm.getGlobalRewardLimit();
		} else {
			value = group.getRewardLimit();
		}
		String valueStr = value + (value == -1 ? " (No limit)" : "");
		return getBase().lore(ChatColor.RED + "Current limit: " + valueStr).build();
	}

	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		parent.getGUIManager().startConversation(player, new RewardLimitPrompt(group));
	}

}
