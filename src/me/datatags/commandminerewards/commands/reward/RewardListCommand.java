package me.datatags.commandminerewards.commands.reward;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.commands.ArgType;

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
		return "If no arguments are specified, lists all reward sections. If one argument, lists the rewards in the reward group.";
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
			if (gcm.getRewardGroups().size() == 0) {
				sender.sendMessage(ChatColor.RED + "There are no defined reward sections.  Add some with /cmr reward add");
				return true;
			}
			sender.sendMessage(ChatColor.GREEN + "The defined reward sections are:  " + gcm.getPrettyRewardSections());
		} else if (args.length == 1) {
			try {
				if (new RewardGroup(args[0]).getChildren().size() == 0) {
					sender.sendMessage(ChatColor.RED + "There are no defined rewards.");
					return true;
				}
				sender.sendMessage(ChatColor.GREEN + "The defined rewards are:  " + new RewardGroup(args[0]).getPrettyChildren());
			} catch (InvalidRewardGroupException e) {
				sender.sendMessage(e.getMessage());
				return true;
			}
		}
		return true;
	}
}
