package me.AlanZ.CommandMineRewards.commands.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.AlanZ.CommandMineRewards.Reward;
import me.AlanZ.CommandMineRewards.Exceptions.CommandAlreadyInListException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.AlanZ.CommandMineRewards.commands.ArgType;

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
		return "Adds a command to a reward section that is executed when the reward is triggered. (don't put a slash before the command.) Placeholder %player% is the player's name. You can use special commands !msg <message> and !title <title> <subtitle> [<fadeIn> <duration> <fadeOut>]. You can put 'none' instead of title or subtitle. Use underscores instead of spaces in title and subtitle, they will be converted to spaces. The last three arguments are time in ticks, and 20 ticks = 1 second. If the last three arguments are not specified, defaults will be used.";
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
		String reward = args[1];
		String command = parseCommand(2, args);
		try {
			new Reward(rewardSection, reward).addCommand(command);
		} catch (InvalidRewardSectionException | InvalidRewardException | CommandAlreadyInListException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return true;
		}
		sender.sendMessage(SUCCESS);
		return true;
	}
}
