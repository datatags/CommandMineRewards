package me.datatags.commandminerewards.gui.buttons.misc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.BlockNotInListException;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class BlockButton extends GUIButton {
	private RewardGroup group;
	private Material block;
	private Boolean data;
	public BlockButton(RewardGroup group, Material block, Boolean data) {
		this.group = group;
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
	protected ItemBuilder build() {
		ItemBuilder ib = new ItemBuilder(block);
		if (data != null) {
			ib.lore(ChatColor.YELLOW + "Data: " + (data ? ChatColor.GREEN : ChatColor.RED) + data.toString());
		}
		ib.lore(ChatColor.RED + "Click to delete");
		return ib;
	}
	
	@Override
	public boolean isButton(ItemStack is) {
		return is.equals(this.getIcon());
	}

	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		CMRLogger.debug("Attempting to remove block");
		try {
			group.removeBlock(block.toString(), data);
		} catch (BlockNotInListException e) {
			e.printStackTrace(); // ???
			return;
		}
		parent.refreshAll();
	}
	
}
