package me.datatags.commandminerewards.commands.reward;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.InvalidRewardException;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.commands.ArgType;

public class RewardRemoveCommand extends RewardCommand {
    @Override
    public String getName() {
        return "remove";
    }
    @Override
    public String getBasicDescription() {
        return "Deletes a reward or reward group";
    }

    @Override
    public String getExtensiveDescription() {
        return "If one argument is specified, deletes an entire reward group. If two, deletes a reward in the specified reward group.";
    }

    @Override
    public String getUsage() {
        return "<rewardSection> [reward]";
    }
    
    @Override
    public String[] getExamples() {
        return new String[] {"cropRewards", "genericRewards smallReward"};
    }
    
    @Override
    public CMRPermission getPermission() {
        return CMRPermission.REWARD_MODIFY;
    }
    @Override
    public int getMinArgs() {
        return 1;
    }
    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public ArgType[] getArgs() {
        return new ArgType[] {ArgType.REWARD_SECTION, ArgType.REWARD};
    }
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            try {
                new RewardGroup(args[0]).delete();
            } catch (InvalidRewardGroupException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + "Reward group successfully removed.");
        } else if (args.length == 2) {
            try {
                new Reward(args[0], args[1]).delete();
            } catch (InvalidRewardGroupException | InvalidRewardException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + "Reward successfully removed!");
        }
        return true;
    }
}
