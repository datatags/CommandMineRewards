package me.datatags.commandminerewards.gui.buttons.area;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.guis.CMRGUI;
import me.datatags.commandminerewards.gui.guis.WorldListGUI;

public class WorldListButton extends AreaListButton {
	public WorldListButton(RewardGroup group) {
		super(group);
	}

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.WORLD;
	}

	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.WORLD_MODIFY;
	}

	@Override
	protected ItemBuilder build() {
		ItemBuilder ib = new ItemBuilder(Material.FILLED_MAP);
		MapMeta mm = (MapMeta) ib.getItemMeta();
		mm.setColor(Color.AQUA);
		ib.name(ChatColor.AQUA + "Allowed Worlds");
		List<String> worlds = generateLore(ib, gcm.getGlobalAllowedWorlds(), group == null ? null : group.getAllowedWorlds());
		for (String world : worlds) {
			ib.lore(ChatColor.BLUE + "- " + world);
		}
		return ib;
	}

	@Override
	public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {
		if (handleRightClick(clickType)) { // if true, the click was handled sufficiently
			holder.updateGUI();
			return;
		}
		parent.getGUIManager().delayOpenGUI(holder, new WorldListGUI(group));
	}

	@Override
	public void setLocalAreas(List<String> areas) {
		group.setAllowedWorlds(areas);
	}

	@Override
	public void setGlobalAreas(List<String> areas) {
		gcm.setGlobalAllowedWorlds(areas);
	}
	
}
