package me.datatags.commandminerewards.gui.buttons.area;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;

public abstract class AreaListButton extends GUIButton {
	protected RewardGroup group;
	protected GlobalConfigManager gcm;
	public AreaListButton(RewardGroup group) {
		this.group = group;
		this.gcm = GlobalConfigManager.getInstance();
	}
	
	protected List<String> generateLore(ItemBuilder ib, List<String> globalAreas, List<String> localAreas) {
		String localLore = ChatColor.LIGHT_PURPLE + "Locally allowed:";
		if (localAreas != null) {
			if (localAreas.size() == 0) {
				ib.lore(ChatColor.YELLOW + "Inherits from global:");
			} else {
				ib.lore(localLore);
				return localAreas;
			}
		}
		if (globalAreas.size() == 0) {
			ib.lore(ChatColor.AQUA + "Not set, defaulting to all allowed");
		} else if (localAreas == null) {
			ib.lore(localLore);
		}
		return globalAreas;
	}
	@Override
	public void addClickableLore(Player player) {
		List<String> lore = base.getLore();
		lore.add(0, ChatColor.RED + "Right-click to clear and inherit");
		lore.add(1, ChatColor.GREEN + "Shift-right-click to allow all");
	}
	public boolean handleRightClick(ClickType click) {
		if (!click.isRightClick()) return false;
		List<String> areas = new ArrayList<>();
		if (click.isShiftClick()) {
			areas.add("*");
		}
		if (group == null) {
			setGlobalAreas(areas);
		} else {
			setLocalAreas(areas);
		}
		return true;
	}
	public abstract void setLocalAreas(List<String> areas);
	public abstract void setGlobalAreas(List<String> areas);
	
}
