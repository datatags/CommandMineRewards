package me.datatags.commandminerewards.commands.silktouch;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.InvalidRewardException;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.commands.ArgType;

public class SilkTouchPolicyGetCommand extends SilkTouchPolicyCommand {
    @Override
    public String getName() {
        return "get";
    }
    @Override
    public String getBasicDescription() {
        return "Gets the silk touch policy.";
    }

    @Override
    public String getExtensiveDescription() {
        return "Gets the silk touch policy globally, per reward group, or per reward. See '/cmr help silktouchpolicy set' for more details.";
    }

    @Override
    public String getUsage() {
        return "[rewardSection] [reward]";
    }
    
    @Override
    public String[] getExamples() {
        return new String[] {"", "genericRewards bigReward"};
    }

    @Override
    public int getMinArgs() {
        return 0;
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
        GlobalConfigManager gcm = GlobalConfigManager.getInstance();
        if (args.length == 0) {
            if (gcm.getGlobalSilkTouchPolicy() == null) {
                sender.sendMessage(ChatColor.RED + "There is no global silk touch requirement, any rewards or reward sections inheriting from the global setting will default to Ignored.");
            } else {
                sender.sendMessage(ChatColor.GREEN + "The global silk touch policy is " + gcm.getGlobalSilkTouchPolicy() + ".  Please note this will be overridden by this setting in any reward sections or rewards.");
            }
            return true;
        }
        String rewardSection = args[0];
        if (args.length == 1) {
            String result = null;
            try {
                RewardGroup rg = new RewardGroup(rewardSection); 
                if (rg.getSilkTouchPolicy() != null) {
                    result = rg.getSilkTouchPolicy().getFriendlyName();
                }
            } catch (InvalidRewardGroupException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
            if (result == null) {
                sender.sendMessage(ChatColor.RED + "This reward group has no defined silk touch requirement, it will be inherited from the global setting.");
            } else {
                sender.sendMessage(ChatColor.GREEN + "The silk touch policy for this reward group is " + result + ".");
            }
        } else if (args.length == 2) {
            String rewardName = args[1];
            String result = null;
            try {
                Reward reward = new Reward(rewardSection, rewardName);
                if (reward.getSilkTouchPolicy() != null) {
                    result = reward.getSilkTouchPolicy().getFriendlyName();
                }
            } catch (InvalidRewardGroupException | InvalidRewardException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
            if (result == null) {
                sender.sendMessage(ChatColor.RED + "This reward has no defined silk touch requirement, it will be inherited from the parent reward group or the global setting.");
            } else {
                sender.sendMessage(ChatColor.GREEN + "The silk touch policy for this reward is " + result + ".");
            }
        }
        return true;
    }
}
