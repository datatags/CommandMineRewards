package me.datatags.commandminerewards.commands.reward;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.Exceptions.InvalidRewardException;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.commands.ArgType;

public class RewardChanceCommand extends RewardCommand {
    
    @Override
    public String getName() {
        return "chance";
    }

    @Override
    public String getBasicDescription() {
        return "Gets or sets the chance of a reward occurring.";
    }

    @Override
    public String getExtensiveDescription() {
        return "Gets or sets the chance of a reward occurring. If only a reward group and reward are specified, gets the chance, if a number is specified as well, sets the chance.";
    }
    
    @Override
    public int getMinArgs() {
        return 2;
    }
    
    @Override
    public int getMaxArgs() {
        return 3;
    }
    
    @Override
    public ArgType[] getArgs() {
        return new ArgType[] {ArgType.REWARD_SECTION, ArgType.REWARD};
    }
    
    @Override
    public String getUsage() {
        return "<rewardSection> <reward> [chance]";
    }
    
    @Override
    public String[] getExamples() {
        return new String[] {"genericRewards smallReward 15"};
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String rewardSection = args[0];
        String reward = args[1];
        if (args.length == 2) {
            double result;
            try {
                result = new Reward(rewardSection, reward).getChance();
            } catch (InvalidRewardGroupException | InvalidRewardException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + "Chance of triggering reward is: " + result + "%");
        } else if (args.length == 3) {
            if (!CMRPermission.REWARD_MODIFY.attempt(sender)) return true;
            double chance;
            try {
                chance = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid number!");
                return true;
            }
            try {
                new Reward(rewardSection, reward).setChance(chance);
            } catch (InvalidRewardGroupException | InvalidRewardException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
            sender.sendMessage(SUCCESS);
        }
        return true;
    }

}
