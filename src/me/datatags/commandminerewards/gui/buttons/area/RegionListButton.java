package me.datatags.commandminerewards.gui.buttons.area;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.guis.CMRGUI;
import me.datatags.commandminerewards.gui.guis.RegionListGUI;
import me.datatags.commandminerewards.worldguard.WorldGuardManager;

public class RegionListButton extends AreaListButton {
	private WorldGuardManager wgm;
	public RegionListButton(RewardGroup group) {
		super(group);
		this.wgm = CommandMineRewards.getInstance().getWGManager();
	}

	@Override
	public CMRPermission getPermission() {
		return wgm.usingWorldGuard() ? CMRPermission.REGION : null;
	}

	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.REGION_MODIFY;
	}

	@Override
	protected ItemBuilder buildBase() {
		ItemBuilder ib = new ItemBuilder(Material.FILLED_MAP);
		MapMeta mm = (MapMeta) ib.getItemMeta();
		mm.setColor(Color.LIME);
		ib.name(ChatColor.GREEN + "Allowed Regions");
		return ib;
	}

	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		ItemBuilder base = getBase();
		for (String region : generateLore(gcm.getGlobalAllowedRegions(), group == null ? null : group.getAllowedRegions())) {
			base.lore(ChatColor.GREEN + "- " + region);
		}
		return base.build();
	}

	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		parent.delayOpenGUI(player, parent.getGUIManager().getGUI(RegionListGUI.class, group, null));
	}

}
