package me.AlanZ.CommandMineRewards.commands.region;

import me.AlanZ.CommandMineRewards.commands.CompoundCommand;

public class RegionCommand extends CompoundCommand {

	@Override
	public String getName() {
		return "region";
	}

	@Override
	public String getBasicDescription() {
		return "Adds, lists, or removes regions in which rewards are allowed to occur.";
	}
	protected boolean isUsingWorldGuard() {
		return getPlugin().usingWorldGuard();
	}

	@Override
	public void init() {
		registerChildren(new RegionAddCommand(), new RegionListCommand(), new RegionRemoveCommand());
	}

}
