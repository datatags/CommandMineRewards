package me.Datatags.CommandMineRewards.gui.buttons.general;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.gui.ItemBuilder;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.conversations.RewardLimitPrompt;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;

public class RewardLimitButton extends GUIButton {
	public RewardSection section;
	public RewardLimitButton(RewardSection section) {
		this.section = section;
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
		if (section == null) {
			value = gcm.getGlobalRewardLimit();
		} else {
			value = section.getRewardLimit();
		}
		return getBase().lore(ChatColor.RED + "Current limit: " + value).build();
	}

	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		parent.getGUIManager().startConversation(player, new RewardLimitPrompt(section));
	}

}
