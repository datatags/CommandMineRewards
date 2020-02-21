package me.AlanZ.CommandMineRewards.commands.block;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.AlanZ.CommandMineRewards.commands.CompoundCommand;

public class BlockCommand extends CompoundCommand {
	@Override
	public String getName() {
		return "block";
	}
	@Override
	public String getBasicDescription() {
		return "View and edit blocks lists";
	}
	public void init() {
		registerChildren(new BlockAddCommand(), new BlockRemoveCommand(), new BlockListCommand());
	}
	protected ItemStack getItemInHand(Player player) {
		return getPlugin().getItemInHand(player);
	}
	@Override
	public String[] getExamples() {
		return new String[] {};
	}
}
