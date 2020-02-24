package me.AlanZ.CommandMineRewards.commands.reward;

import me.AlanZ.CommandMineRewards.commands.CompoundCommand;

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
