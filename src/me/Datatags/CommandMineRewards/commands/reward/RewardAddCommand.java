package me.Datatags.CommandMineRewards.commands.reward;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.Datatags.CommandMineRewards.Reward;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.Datatags.CommandMineRewards.Exceptions.RewardAlreadyExistsException;
import me.Datatags.CommandMineRewards.Exceptions.RewardSectionAlreadyExistsException;
import me.Datatags.CommandMineRewards.commands.ArgType;

public class RewardAddCommand extends RewardCommand {
	@Override
	public String getName() {
		return "add";
	}
	@Override
	public String getBasicDescription() {
		return "Create a reward or reward section";
	}

	@Override
	public String getExtensiveDescription() {
		return "If one argument is specified, creates a reward section. If two, creates a reward in an existing reward section.";
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
		return new ArgType[] {ArgType.REWARD_SECTION};
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		String section = args[0];
		if (args.length == 1) {
			try {
				new RewardSection(section, true);
			} catch (InvalidRewardSectionException | RewardSectionAlreadyExistsException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			sender.sendMessage(ChatColor.GREEN + "Reward section creation complete, add some rewards with /cmr reward add " + section + " <reward> and some blocks with /cmr block add " + section + " [block]");
		} else if (args.length == 2) {
			String name = args[1];
			try {
				new Reward(section, name, true);
			} catch (RewardAlreadyExistsException | InvalidRewardException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			sender.sendMessage(ChatColor.YELLOW + "Reward creation complete, use /cmr command add " + section + " " + name + " <command> to add commands to this reward, and /cmr reward chance " + section + " " + name + " <chance> to set the chance.");
		}
		return true;
	}
}
