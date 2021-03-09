package me.Datatags.CommandMineRewards.gui.buttons.rewardsection;

import java.util.Map;
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
import me.Datatags.CommandMineRewards.gui.guis.BlockListGUI;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;

public class BlockListButton extends GUIButton {
	private RewardSection section;
	public BlockListButton(RewardSection section) {
		this.section = section;
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.BLOCK;
	}

	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.BLOCK; // you can still view the block list without modify permission
	}

	@Override
	protected ItemBuilder buildBase() {
		return new ItemBuilder(Material.GRASS_BLOCK).name(ChatColor.GOLD + "Block List");
	}

	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		ItemBuilder ib = getBase();
		int i = 0;
		Map<String,Boolean> blocks = section.getBlocksWithData();
		for (Entry<String,Boolean> entry : blocks.entrySet()) {
			if (entry.getValue() == null) {
				ib.lore(entry.getKey());
			} else {
				ib.lore(entry.getKey() + ":" + entry.getValue());
			}
			if (++i >= 10 && blocks.size() > 10) {
				ib.lore("...and " + (blocks.size() - 10) + " more...");
				break;
			}
		}
		return ib.build();
	}

	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		parent.getGUIManager().getGUI(BlockListGUI.class, section, null).openFor(player);
	}

}
