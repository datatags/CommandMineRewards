package me.AlanZ.CommandMineRewards.commands.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.AlanZ.CommandMineRewards.Reward;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.AlanZ.CommandMineRewards.commands.ArgType;

public class CmdListCommand extends CmdCommand {
	@Override
	public String getName() {
		return "list";
	}
	@Override
	public String getBasicDescription() {
		return "Lists all reward commands in a reward";
	}

	@Override
	public String getExtensiveDescription() {
		return "Lists all reward commands in a reward. They are prefixed by IDs that you can use for inserting and removing commands.";
	}

	@Override
	public String getUsage() {
		return "<rewardSection> <reward>";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"genericRewards smallReward"};
	}

	@Override
	public int getMinArgs() {
		return 2;
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
		String rewardSection = args[0];
		String rewardName = args[1];
		Reward reward;
		try {
			reward = new Reward(rewardSection, rewardName);
		} catch (InvalidRewardSectionException | InvalidRewardException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return true;
		}
		if (reward.getCommands().size() == 0) {
			sender.sendMessage(ChatColor.RED + "There are no commands in that reward.  Add some with /cmr addcommand " + rewardSection + " " + reward.getName() + " <command>");
		} else {
			for (int i = 0; i < reward.getCommands().size(); i++) {
				sender.sendMessage(i + ": /" + reward.getCommands().get(i).getCommand());
			}
		}
		return true;
	}
}
