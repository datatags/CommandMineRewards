package me.Datatags.CommandMineRewards.commands.reward;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.Datatags.CommandMineRewards.Reward;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.Datatags.CommandMineRewards.commands.ArgType;

public class RewardRemoveCommand extends RewardCommand {
	@Override
	public String getName() {
		return "remove";
	}
	@Override
	public String getBasicDescription() {
		return "Deletes a reward or reward section";
	}

	@Override
	public String getExtensiveDescription() {
		return "If one argument is specified, deletes an entire reward section. If two, deletes a reward in the specified reward section.";
	}

	@Override
	public String getUsage() {
		return "<rewardSection> [reward]";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"cropRewards", "genericRewards smallReward"};
	}
	
	@Override
	public boolean isModifier() {
		return true;
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
		return new ArgType[] {ArgType.REWARD_SECTION, ArgType.REWARD};
	}
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (args.length == 1) {
			try {
				new RewardSection(args[0]).delete();
			} catch (InvalidRewardSectionException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			sender.sendMessage(ChatColor.GREEN + "Reward section successfully removed.");
		} else if (args.length == 2) {
			try {
				new Reward(args[0], args[1]).delete();
			} catch (InvalidRewardSectionException | InvalidRewardException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			sender.sendMessage(ChatColor.GREEN + "Reward successfully removed!");
		}
		return true;
	}
}
