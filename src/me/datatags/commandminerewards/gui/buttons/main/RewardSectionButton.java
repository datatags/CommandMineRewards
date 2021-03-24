package me.datatags.commandminerewards.gui.buttons.main;

import java.util.Map.Entry;

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
import me.datatags.commandminerewards.gui.guis.CMRGUI;
import me.datatags.commandminerewards.gui.guis.RewardSectionGUI;

public class RewardSectionButton extends GUIButton {
	private RewardGroup group;
	public RewardSectionButton(RewardGroup group) {
		this.group = group;
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.REWARD;
	}
	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.REWARD; // not REWARD_MODIFY because clicking the button does not modify the reward group
	}
	@Override
	protected ItemBuilder buildBase() {
		// TODO: reward group icon is first block in list
		ItemBuilder ib = new ItemBuilder(Material.BOOKSHELF).name(ChatColor.YELLOW + group.getName());
		ib.lore(ChatColor.GREEN + "Rewards:");
		if (group.getChildren().size() == 0) {
			ib.lore(ChatColor.RED + "- None");
		} else {
			for (String child : group.getChildrenNames()) {
				ib.lore(ChatColor.DARK_GREEN + "- " + child);
			}
		}
		ib.lore("");
		ib.lore(ChatColor.BLUE + "Blocks:");
		int i = 0;
		if (group.getBlocks().size() == 0) {
			ib.lore(ChatColor.RED + "- None");
		} else {
			for (Entry<String,Boolean> entry : group.getBlocksWithData().entrySet()) {
				if (entry.getValue() == null) {
					ib.lore(ChatColor.DARK_BLUE + "- " + entry.getKey());
				} else {
					ib.lore(ChatColor.DARK_BLUE + "- " + entry.getKey() + ":" + entry.getValue());
				}
				if (++i >= 10 && group.getBlocksWithData().size() > 10) { // loop breaks anyway if second condition fails
					ib.lore(ChatColor.BLUE + "... and " + (group.getBlocksWithData().size() - 10) + " more ...");
					break;
				}
			}
		}
		if (group.getChildrenNames().size() == 0) {
			ib.lore(ChatColor.RED + "Right-click to delete");
		} else {
			ib.lore(ChatColor.YELLOW + "You must delete all rewards under");
			ib.lore(ChatColor.YELLOW + "this group before deleting.");
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
			parent.getGUIManager().getGUI(RewardSectionGUI.class, group, null).openFor(player);
		} else if (clickType.isRightClick() && group.getChildrenNames().size() == 0) {
			group.delete();
		}
	}
	public RewardGroup getRewardGroup() {
		return group;
	}
	
}
