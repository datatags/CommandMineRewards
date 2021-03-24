package me.datatags.commandminerewards.gui.buttons.area;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
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
	protected ItemBuilder buildBase() {
		ItemBuilder ib = new ItemBuilder(Material.FILLED_MAP);
		MapMeta mm = (MapMeta) ib.getItemMeta();
		mm.setColor(Color.AQUA);
		ib.name(ChatColor.AQUA + "Allowed Worlds");
		return ib;
	}

	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		ItemBuilder base = getBase();
		for (String world : generateLore(gcm.getGlobalAllowedWorlds(), group == null ? null : group.getAllowedWorlds())) {
			base.lore(ChatColor.BLUE + "- " + world);
		}
		return base.build();
	}

	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		parent.delayOpenGUI(player, parent.getGUIManager().getGUI(WorldListGUI.class, group, null));
	}
	
}
