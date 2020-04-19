package me.Datatags.CommandMineRewards.commands.region;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.Datatags.CommandMineRewards.commands.ArgType;

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
				sender.sendMessage(ChatColor.GREEN + "The allowed regions are:  " + gcm.makePretty(rs.getAllowedRegions()));
			}
		}
		return true;
	}
}
