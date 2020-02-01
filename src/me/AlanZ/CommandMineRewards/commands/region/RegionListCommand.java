package me.AlanZ.CommandMineRewards.commands.region;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.AlanZ.CommandMineRewards.GlobalConfigManager;
import me.AlanZ.CommandMineRewards.RewardSection;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.AlanZ.CommandMineRewards.commands.ArgType;

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
		return "Lists regions in which rewards under a reward section, or globally, are allowed to occur. See '/cmr help region add' for more details.";
	}

	@Override
	public String getUsage() {
		return "[rewardSection]";
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
		if (args.length == 0) {
			if (GlobalConfigManager.getGlobalAllowedRegions().size() == 0) {
				sender.sendMessage(ChatColor.RED + "There are no globally allowed regions.");
			} else {
				sender.sendMessage(ChatColor.GREEN + "The globally allowed regions are:  " + GlobalConfigManager.makePretty(GlobalConfigManager.getGlobalAllowedRegions()));
			}
		} else if (args.length == 1) {
			RewardSection rs;
			try {
				rs = new RewardSection(args[0]);
			} catch (InvalidRewardSectionException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			if (rs.getAllowedRegions().size() == 0) {
				sender.sendMessage(ChatColor.YELLOW + "There are no defined allowed regions in this reward section.  The rewards region checker will use the global ones.");
			} else {
				sender.sendMessage(ChatColor.GREEN + "The allowed regions are:  " + GlobalConfigManager.makePretty(rs.getAllowedRegions()));
			}
		}
		return true;
	}
}
