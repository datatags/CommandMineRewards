package me.AlanZ.CommandMineRewards.commands.world;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.AlanZ.CommandMineRewards.GlobalConfigManager;
import me.AlanZ.CommandMineRewards.RewardSection;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.AlanZ.CommandMineRewards.commands.ArgType;

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
		return "Lists all worlds in which rewards are allowed to occur. With no arguments it lists global worlds, with a reward section as an argument it lists worlds under that section.";
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
			RewardSection rs;
			try {
				rs = new RewardSection(args[0]);
			} catch (InvalidRewardSectionException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			if (rs.getAllowedWorlds().size() == 0) {
				sender.sendMessage(ChatColor.YELLOW + "There are no defined allowed worlds in this reward section.  The rewards world checker will use the global ones.");
			} else {
				sender.sendMessage(ChatColor.GREEN + "The allowed worlds are:  " + gcm.makePretty(rs.getAllowedWorlds()));
			}
		}
		return true;
	}
}
