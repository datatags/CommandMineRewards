package me.AlanZ.CommandMineRewards.commands.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.AlanZ.CommandMineRewards.Reward;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.AlanZ.CommandMineRewards.commands.ArgType;

public class CmdReplaceCommand extends CmdCommand {
	@Override
	public String getName() {
		return "replace";
	}
	@Override
	public String getBasicDescription() {
		return "Replaces a command to be executed as a reward.";
	}

	@Override
	public String getExtensiveDescription() {
		return "Replaces an existing index in a reward command list, in case you didn't get it quite right the first time.  Same as cmd remove X and cmd insert X.";
	}

	@Override
	public String getUsage() {
		return "<rewardSection> <reward> <index> <command>";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"genericRewards bigReward 1 eco take %player% 150"};
	}

	@Override
	public int getMinArgs() {
		return 4;
	}

	@Override
	public int getMaxArgs() {
		return -1;
	}
	@Override
	public boolean isModifier() {
		return true;
	}
	@Override
	public ArgType[] getArgs() {
		return new ArgType[] {ArgType.REWARD_SECTION, ArgType.REWARD};
	}
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		String rewardSection = args[0];
		String rewardName = args[1];
		int index;
		try {
			index = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
			return true;
		}
		String command = parseCommand(3, args);
		try {
			Reward reward = new Reward(rewardSection, rewardName);
			reward.removeCommand(index);
			reward.insertCommand(command, index);
		} catch (InvalidRewardSectionException | InvalidRewardException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return true;
		}
		sender.sendMessage(SUCCESS);
		return true;
	}
}
