package me.Datatags.CommandMineRewards.gui.buttons.misc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.Exceptions.BlockNotInListException;
import me.Datatags.CommandMineRewards.gui.ItemBuilder;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;

public class BlockButton extends GUIButton {
	private RewardSection section;
	private Material block;
	private Boolean data;
	public BlockButton(RewardSection section, Material block, Boolean data) {
		this.section = section;
		this.block = block;
		this.data = data;
	}

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.GUI;
	}

	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.BLOCK_MODIFY;
	}

	@Override
	protected ItemBuilder buildBase() {
		ItemBuilder ib = new ItemBuilder(block);
		if (data != null) {
			ib.lore(ChatColor.YELLOW + "Data: " + (data ? ChatColor.GREEN : ChatColor.RED) + data.toString());
		}
		return ib;
	}

	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		return getBase().build();
	}

	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		if (clickType.isRightClick()) {
			try {
				section.removeBlock(block.toString(), data);
			} catch (BlockNotInListException e) {
				e.printStackTrace();
			}
			parent.refreshGUI(player);
		}
	}
	
}
