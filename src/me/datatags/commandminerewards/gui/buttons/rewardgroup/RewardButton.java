package me.datatags.commandminerewards.gui.buttons.rewardgroup;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.commands.RewardCommandEntry;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.guis.CMRGUI;
import me.datatags.commandminerewards.gui.guis.RewardGUI;

public class RewardButton extends GUIButton {
	private Reward reward;
	public RewardButton(Reward reward) {
		this.reward = reward;
	}

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.REWARD;
	}

	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.REWARD;
	}

	@Override
	protected ItemBuilder buildBase() {
		ItemBuilder ib = new ItemBuilder(Material.PAPER).name(ChatColor.LIGHT_PURPLE + reward.getName());
		ib.lore(ChatColor.BLUE + "Base chance: " + reward.getChance());
		ib.lore(ChatColor.LIGHT_PURPLE + "Commands:");
		if (reward.getCommands().size() == 0) {
			ib.lore(ChatColor.RED + "- None");
		} else {
			int i = 0;
			int howMany = reward.getCommands().size();
			for (RewardCommandEntry cmd : reward.getCommands()) {
				ib.lore(ChatColor.LIGHT_PURPLE + "- " + cmd.getCommand());
				if (++i > 10 && howMany > 10) {
					ib.lore(ChatColor.LIGHT_PURPLE + "...and " + (howMany - 10) + " more...");
					break;
				}
			}
		}
		return ib;
	}

	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		return getBase().build();
	}

	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		parent.delayOpenGUI(player, parent.getGUIManager().getGUI(RewardGUI.class, reward.getParent(), reward));
	}
	
	public Reward getReward() {
		return reward;
	}

}
