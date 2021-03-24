package me.Datatags.CommandMineRewards.gui.buttons.area;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.CommandMineRewards;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.RewardGroup;
import me.Datatags.CommandMineRewards.gui.ItemBuilder;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;
import me.Datatags.CommandMineRewards.gui.guis.RegionListGUI;
import me.Datatags.CommandMineRewards.worldguard.WorldGuardManager;

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
