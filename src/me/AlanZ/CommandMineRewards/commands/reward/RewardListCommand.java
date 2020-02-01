package me.AlanZ.CommandMineRewards.commands.reward;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.AlanZ.CommandMineRewards.GlobalConfigManager;
import me.AlanZ.CommandMineRewards.RewardSection;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.AlanZ.CommandMineRewards.commands.ArgType;

public class RewardListCommand extends RewardCommand {
	@Override
	public String getName() {
		return "list";
	}
	
	@Override
	public String getBasicDescription() {
		return "Lists rewards or reward sections";
	}

	@Override
	public String getExtensiveDescription() {
		return "If no arguments are specified, lists all reward sections. If one argument, lists the rewards in the reward section.";
	}

	@Override
	public String getUsage() {
		return "[rewardSection]";
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
		if (args.length == 0) {
			if (GlobalConfigManager.getRewardSections().size() == 0) {
				sender.sendMessage(ChatColor.RED + "There are no defined reward sections.  Add some with /cmr reward add");
				return true;
			}
			sender.sendMessage(ChatColor.GREEN + "The defined reward sections are:  " + GlobalConfigManager.getPrettyRewardSections());
		} else if (args.length == 1) {
			try {
				if (new RewardSection(args[0]).getChildren().size() == 0) {
					sender.sendMessage(ChatColor.RED + "There are no defined rewards.");
					return true;
				}
				sender.sendMessage(ChatColor.GREEN + "The defined rewards are:  " + new RewardSection(args[0]).getPrettyChildren());
			} catch (InvalidRewardSectionException e) {
				sender.sendMessage(e.getMessage());
				return true;
			}
		}
		return true;
	}
}
