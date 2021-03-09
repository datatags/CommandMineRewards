package me.Datatags.CommandMineRewards.gui.buttons.main;

import java.util.Map.Entry;

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
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;
import me.Datatags.CommandMineRewards.gui.guis.RewardSectionGUI;

public class RewardSectionButton extends GUIButton {
	private RewardSection section;
	public RewardSectionButton(RewardSection section) {
		this.section = section;
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.REWARD;
	}
	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.REWARD; // not REWARD_MODIFY because clicking the button does not modify the reward section
	}
	@Override
	protected ItemBuilder buildBase() {
		// TODO: reward section icon is first block in list
		ItemBuilder ib = new ItemBuilder(Material.BOOKSHELF).name(ChatColor.YELLOW + section.getName());
		ib.lore(ChatColor.GREEN + "Rewards:");
		if (section.getChildren().size() == 0) {
			ib.lore(ChatColor.RED + "- None");
		} else {
			for (String child : section.getChildrenNames()) {
				ib.lore(ChatColor.DARK_GREEN + "- " + child);
			}
		}
		ib.lore("");
		ib.lore(ChatColor.BLUE + "Blocks:");
		int i = 0;
		if (section.getBlocks().size() == 0) {
			ib.lore(ChatColor.RED + "- None");
		} else {
			for (Entry<String,Boolean> entry : section.getBlocksWithData().entrySet()) {
				if (entry.getValue() == null) {
					ib.lore(ChatColor.DARK_BLUE + "- " + entry.getKey());
				} else {
					ib.lore(ChatColor.DARK_BLUE + "- " + entry.getKey() + ":" + entry.getValue());
				}
				if (++i >= 10 && section.getBlocksWithData().size() > 10) { // loop breaks anyway if second condition fails
					ib.lore(ChatColor.BLUE + "... and " + (section.getBlocksWithData().size() - 10) + " more ...");
					break;
				}
			}
		}
		if (section.getChildrenNames().size() == 0) {
			ib.lore(ChatColor.RED + "Right-click to delete");
		} else {
			ib.lore(ChatColor.YELLOW + "You must delete all rewards under");
			ib.lore(ChatColor.YELLOW + "this section before deleting.");
		}
		return ib;
	}
	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		return getBase().build();
	}
	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		if (clickType.isLeftClick()) {
			parent.getGUIManager().getGUI(RewardSectionGUI.class, section, null).openFor(player);
		} else if (clickType.isRightClick() && section.getChildrenNames().size() == 0) {
			section.delete();
		}
	}
	public RewardSection getRewardSection() {
		return section;
	}
	
}
