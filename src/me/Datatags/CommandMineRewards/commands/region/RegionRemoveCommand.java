package me.Datatags.CommandMineRewards.commands.region;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.Datatags.CommandMineRewards.Exceptions.AreaNotInListException;
import me.Datatags.CommandMineRewards.commands.ArgType;

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
	public CMRPermission getPermission() {
		return CMRPermission.REGION_MODIFY;
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
				GlobalConfigManager.getInstance().removeGlobalAllowedRegion(region);
			} catch (AreaNotInListException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
		} else if (args.length == 2) {
			String rewardSection = args[1];
			try {
				new RewardSection(rewardSection).removeAllowedRegion(region);
			} catch (InvalidRewardSectionException | AreaNotInListException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
		}
		sender.sendMessage(SUCCESS);
		return true;
	}
}
