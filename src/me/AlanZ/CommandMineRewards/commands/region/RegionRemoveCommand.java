package me.AlanZ.CommandMineRewards.commands.region;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.AlanZ.CommandMineRewards.GlobalConfigManager;
import me.AlanZ.CommandMineRewards.RewardSection;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.AlanZ.CommandMineRewards.Exceptions.RegionNotInListException;
import me.AlanZ.CommandMineRewards.commands.ArgType;

public class RegionRemoveCommand extends RegionCommand {
	@Override
	public String getName() {
		return "remove";
	}
	@Override
	public String getBasicDescription() {
		return "Removes an allowed region";
	}

	@Override
	public String getExtensiveDescription() {
		return "Removes a region in which rewards under a reward section, or globally, are allowed to occur. See '/cmr help region add' for more details";
	}

	@Override
	public String getUsage() {
		return "<region> [rewardSection]";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"superMine genericRewards"};
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
		return new ArgType[] {ArgType.REGION, ArgType.REWARD_SECTION};
	}
	@Override
	public boolean isModifier() {
		return true;
	}
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (!isUsingWorldGuard()) {
			sender.sendMessage(ChatColor.RED + "Region commands are disabled because WorldGuard was not found.");
			return true;
		}
		String region = args[0];
		if (args.length == 1) {
			try {
				GlobalConfigManager.removeGlobalAllowedRegion(region);
			} catch (RegionNotInListException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
		} else if (args.length == 2) {
			String rewardSection = args[1];
			try {
				new RewardSection(rewardSection).removeAllowedRegion(region);
			} catch (InvalidRewardSectionException | RegionNotInListException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
		}
		sender.sendMessage(SUCCESS);
		return true;
	}
}
