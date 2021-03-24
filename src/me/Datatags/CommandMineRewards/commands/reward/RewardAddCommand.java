package me.Datatags.CommandMineRewards.commands.reward;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.Reward;
import me.Datatags.CommandMineRewards.RewardGroup;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardGroupException;
import me.Datatags.CommandMineRewards.Exceptions.RewardAlreadyExistsException;
import me.Datatags.CommandMineRewards.Exceptions.RewardGroupAlreadyExistsException;
import me.Datatags.CommandMineRewards.commands.ArgType;

public class RewardAddCommand extends RewardCommand {
	@Override
	public String getName() {
		return "add";
	}
	@Override
	public String getBasicDescription() {
		return "Create a reward or reward group";
	}

	@Override
	public String getExtensiveDescription() {
		return "If one argument is specified, creates a reward group. If two, creates a reward in an existing reward group.";
	}

	@Override
	public String getUsage() {
		return "<rewardSection> [reward]";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"miningRewards", "genericRewards superMegaReward"};
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.REWARD_MODIFY;
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
		return new ArgType[] {ArgType.REWARD_SECTION};
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		String group = args[0];
		if (args.length == 1) {
			try {
				new RewardGroup(group, true);
			} catch (InvalidRewardGroupException | RewardGroupAlreadyExistsException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			sender.sendMessage(ChatColor.GREEN + "Reward group creation complete, add some rewards with /cmr reward add " + group + " <reward> and some blocks with /cmr block add " + group + " [block]");
		} else if (args.length == 2) {
			String name = args[1];
			try {
				new Reward(group, name, true);
			} catch (RewardAlreadyExistsException | InvalidRewardException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			sender.sendMessage(ChatColor.YELLOW + "Reward creation complete, use /cmr command add " + group + " " + name + " <command> to add commands to this reward, and /cmr reward chance " + group + " " + name + " <chance> to set the chance.");
		}
		return true;
	}
}
