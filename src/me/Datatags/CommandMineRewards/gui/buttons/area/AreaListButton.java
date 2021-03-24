package me.Datatags.CommandMineRewards.gui.buttons.area;

import java.util.List;

import org.bukkit.ChatColor;

import me.Datatags.CommandMineRewards.RewardGroup;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;

public abstract class AreaListButton extends GUIButton {
	protected RewardGroup group;
	public AreaListButton(RewardGroup group) {
		this.group = group;
	}
	
	protected List<String> generateLore(List<String> globalAreas, List<String> localAreas) {
		if (localAreas != null) {
			if (localAreas.size() == 0) {
				base.lore(ChatColor.YELLOW + "Inherits from global:");
			} else {
				return localAreas;
			}
		} else {
			base.lore(ChatColor.LIGHT_PURPLE + "Locally allowed:");
		}
		return globalAreas;
	}
	
}
