package me.Datatags.CommandMineRewards.commands.reward;

import me.Datatags.CommandMineRewards.commands.CompoundCommand;

public class RewardCommand extends CompoundCommand {

	@Override
	public String getName() {
		return "reward";
	}
	
	@Override
	public String getBasicDescription() {
		return "Creates, shows, and deletes rewards.";
	}

	@Override
	public void init() {
		registerChildren(new RewardAddCommand(), new RewardChanceCommand(), new RewardListCommand(), new RewardRemoveCommand());
	}

}
