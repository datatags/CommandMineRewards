package me.Datatags.CommandMineRewards.commands.region;

import me.Datatags.CommandMineRewards.commands.CompoundCommand;

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
		return getPlugin().getWGManager().usingWorldGuard();
	}

	@Override
	public void init() {
		registerChildren(new RegionAddCommand(), new RegionListCommand(), new RegionRemoveCommand());
	}

}
