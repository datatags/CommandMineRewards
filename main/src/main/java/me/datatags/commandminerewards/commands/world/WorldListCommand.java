package me.datatags.commandminerewards.commands.world;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.commands.ArgType;

public class WorldListCommand extends WorldCommand {
    @Override
    public String getName() {
        return "list";
    }
    @Override
    public String getBasicDescription() {
        return "Lists allowed worlds";
    }

    @Override
    public String getExtensiveDescription() {
        return "Lists all worlds in which rewards are allowed to occur. With no arguments it lists global worlds, with a reward group as an argument it lists worlds under that group.";
    }

    @Override
    public String getUsage() {
        return "[rewardSection]";
    }
    
    @Override
    public String[] getExamples() {
        return new String[] {"genericRewards"};
    }

    @Override
    public int getMinArgs() {
        return 0;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public ArgType[] getArgs() {
        return new ArgType[] {ArgType.REWARD_SECTION};
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        GlobalConfigManager gcm = GlobalConfigManager.getInstance();
        if (args.length == 0) {
            if (gcm.getGlobalAllowedWorlds().size() == 0) {
                sender.sendMessage(ChatColor.RED + "There are no globally allowed worlds.");
            } else {
                sender.sendMessage(ChatColor.GREEN + "The globally allowed worlds are:  " + gcm.makePretty(gcm.getGlobalAllowedWorlds()));
            }
        } else if (args.length == 1) {
            RewardGroup rg;
            try {
                rg = new RewardGroup(args[0]);
            } catch (InvalidRewardGroupException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
            if (rg.getAllowedWorlds().size() == 0) {
                sender.sendMessage(ChatColor.YELLOW + "There are no defined allowed worlds in this reward group.  The rewards world checker will use the global ones.");
            } else {
                sender.sendMessage(ChatColor.GREEN + "The allowed worlds are:  " + gcm.makePretty(rg.getAllowedWorlds()));
            }
        }
        return true;
    }
}
