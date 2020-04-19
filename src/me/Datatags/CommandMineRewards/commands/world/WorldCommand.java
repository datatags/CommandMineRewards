package me.Datatags.CommandMineRewards.commands.world;

import me.Datatags.CommandMineRewards.commands.CompoundCommand;

public class WorldCommand extends CompoundCommand {

	@Override
	public String getName() {
		return "world";
	}

	@Override
	public String getBasicDescription() {
		return "Add, remove, or list worlds in which rewards can occur.";
	}

	@Override
	public void init() {
		registerChildren(new WorldAddCommand(), new WorldListCommand(), new WorldRemoveCommand());
	}
	
}
