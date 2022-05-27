package me.datatags.commandminerewards.commands.reward;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.commands.CompoundCommand;

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
    @Override
    public CMRPermission getPermission() {
        return CMRPermission.REWARD;
    }

}
