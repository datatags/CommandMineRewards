package me.Datatags.CommandMineRewards.commands.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.Datatags.CommandMineRewards.Reward;
import me.Datatags.CommandMineRewards.Exceptions.CommandNotInListException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.Datatags.CommandMineRewards.commands.ArgType;

public class CmdRemoveCommand extends CmdCommand {
	@Override
	public String getName() {
		return "remove";
	}
	@Override
	public String getBasicDescription() {
		return "Removes a reward command";
	}

	@Override
	public String getExtensiveDescription() {
		return "Removes a reward command from the selected reward. You can remove by ID or typing the full command, but ID is recommended. You can get the ID with /cmr command list";
	}

	@Override
	public String getUsage() {
		return "<rewardSection> <reward> <index|command>";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"genericRewards bigReward 2", "cropRewards smallReward eco give %player% 100"};
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
	public ArgType[] getArgs() {
		return new ArgType[] {ArgType.REWARD_SECTION, ArgType.REWARD};
	}
	@Override
	public boolean isModifier() {
		return true;
	}
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		String rewardSection = args[0];
		String rewardName = args[1];
		Reward reward;
		try {
			reward = new Reward(rewardSection, rewardName);
		} catch (InvalidRewardSectionException | InvalidRewardException ex) {
			sender.sendMessage(ChatColor.RED + ex.getMessage());
			return true;
		}
		if (args.length == 3) {
			int index;
			try {
				index = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				try {
					reward.removeCommand(args[2]);
				} catch (ArrayIndexOutOfBoundsException | CommandNotInListException ex) {
					sender.sendMessage(ChatColor.RED + ex.getMessage());
					return true;
				}
				sender.sendMessage(ChatColor.GREEN + "Successfully removed command!");
				return true;
			}
			try {
				reward.removeCommand(index);
			} catch (ArrayIndexOutOfBoundsException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			sender.sendMessage(SUCCESS);
		} else if (args.length > 3) {
			String command = parseCommand(2, args);
			try {
				reward.removeCommand(command);
			} catch (CommandNotInListException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			sender.sendMessage(ChatColor.GREEN + "Successfully removed command!");
		}
		return true;
	}
}
