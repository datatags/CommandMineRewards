package me.datatags.commandminerewards.gui.buttons.general;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.conversations.CMRConversationFactory;
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
	protected ItemBuilder build() {
		return new ItemBuilder(Material.EMERALD_BLOCK).name(ChatColor.GREEN + "New reward" + (group == null ? " group" : ""));
	}
	
	@Override
	public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {
		CMRConversationFactory.startConversation(holder, new RewardNamePrompt(group));
	}

}
