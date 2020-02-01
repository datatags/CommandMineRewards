package me.AlanZ.CommandMineRewards.commands.block;

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
}
