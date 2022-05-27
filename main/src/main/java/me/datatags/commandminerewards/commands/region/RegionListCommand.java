package me.datatags.commandminerewards.commands.region;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.commands.ArgType;

public class RegionListCommand extends RegionCommand {
    @Override
    public String getName() {
        return "list";
    }
    @Override
    public String getBasicDescription() {
        return "Lists allowed regions";
    }

    @Override
    public String getExtensiveDescription() {
        return "Lists regions in which rewards under a reward group, or globally, are allowed to occur. See '/cmr help region add' for more details.";
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
        if (!isUsingWorldGuard()) {
            sender.sendMessage(ChatColor.RED + "Region commands are disabled because WorldGuard was not found.");
            return true;
        }
        GlobalConfigManager gcm = GlobalConfigManager.getInstance();
        if (args.length == 0) {
            if (gcm.getGlobalAllowedRegions().size() == 0) {
                sender.sendMessage(ChatColor.RED + "There are no globally allowed regions.");
            } else {
                sender.sendMessage(ChatColor.GREEN + "The globally allowed regions are:  " + gcm.makePretty(gcm.getGlobalAllowedRegions()));
            }
        } else if (args.length == 1) {
            RewardGroup rg;
            try {
                rg = new RewardGroup(args[0]);
            } catch (InvalidRewardGroupException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return true;
            }
            if (rg.getAllowedRegions().size() == 0) {
                sender.sendMessage(ChatColor.YELLOW + "There are no defined allowed regions in this reward group.  The rewards region checker will use the global ones.");
            } else {
                sender.sendMessage(ChatColor.GREEN + "The allowed regions are:  " + gcm.makePretty(rg.getAllowedRegions()));
            }
        }
        return true;
    }
}
