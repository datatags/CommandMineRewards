package me.Datatags.CommandMineRewards.commands.reward;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.Datatags.CommandMineRewards.commands.ArgType;

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
	public String[] getExamples() {
		return new String[] {"", "genericRewards"};
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
			if (gcm.getRewardSections().size() == 0) {
				sender.sendMessage(ChatColor.RED + "There are no defined reward sections.  Add some with /cmr reward add");
				return true;
			}
			sender.sendMessage(ChatColor.GREEN + "The defined reward sections are:  " + gcm.getPrettyRewardSections());
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
