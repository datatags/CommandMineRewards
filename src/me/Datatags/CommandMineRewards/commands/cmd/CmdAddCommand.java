package me.Datatags.CommandMineRewards.commands.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.Reward;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.Datatags.CommandMineRewards.commands.ArgType;

public class CmdAddCommand extends CmdCommand {
	@Override
	public String getName() {
		return "add";
	}
	@Override
	public String getBasicDescription() {
		return "Adds a command to be executed as a reward.";
	}

	@Override
	public String getExtensiveDescription() {
		return "Adds a command to a reward section that is executed when the reward is triggered. (don't put a slash before the command.) Placeholder %player% is the player's name. You can also use a few special commands, see more info with /cmr help title, /cmr help sound, and /cmr help msg.";
	}

	@Override
	public String getUsage() {
		return "<rewardSection> <reward> <command>";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"genericRewards bigReward eco take %player% 15"};
	}

	@Override
	public int getMinArgs() {
		return 3;
	}

	@Override
	public int getMaxArgs() {
		return -1;
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.COMMAND_MODIFY;
	}
	@Override
	public ArgType[] getArgs() {
		return new ArgType[] {ArgType.REWARD_SECTION, ArgType.REWARD};
	}
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		String rewardSection = args[0];
		String reward = args[1];
		String command = parseCommand(2, args);
		try {
			new Reward(rewardSection, reward).addCommand(command);
		} catch (InvalidRewardSectionException | InvalidRewardException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return true;
		}
		sender.sendMessage(SUCCESS);
		return true;
	}
}
