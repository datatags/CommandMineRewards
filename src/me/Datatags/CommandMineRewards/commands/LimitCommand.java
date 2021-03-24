package me.Datatags.CommandMineRewards.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.RewardGroup;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardGroupException;

public class LimitCommand extends CMRCommand {
	@Override
	public String getName() {
		return "limit";
	}

	@Override
	public String getBasicDescription() {
		return "Sets or gets reward limits";
	}

	@Override
	public String getExtensiveDescription() {
		return "Sets the maximum amount of rewards received per block broken, globally or per-group. Set to -1 to disable.";
	}
	
	@Override
	public String getUsage() {
		return "[rewardSection] [newValue]";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"exampleReward1", "exampleReward2 -1", "2"};
	}

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.LIMIT;
	}
	
	@Override
	public int getMinArgs() {
		return 0;
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
		GlobalConfigManager gcm = GlobalConfigManager.getInstance();
		if (args.length == 0) {
			sender.sendMessage(ChatColor.GREEN + "The global reward limit is currently " + gcm.getGlobalRewardLimit());
			return true;
		} else if (args.length == 1) {
			try {
				int newValue = Integer.parseInt(args[0]);
				if (!CMRPermission.LIMIT_MODIFY.attempt(sender)) return true;
				gcm.setGlobalRewardLimit(newValue);
				sender.sendMessage(ChatColor.GREEN + "Success!");
				return true;
			} catch (NumberFormatException e) {
				
			}
		}
		RewardGroup group;
		try {
			group = new RewardGroup(args[0]);
		} catch (InvalidRewardGroupException ex) {
			sender.sendMessage(ChatColor.RED + ex.getMessage());
			return true;
		}
		if (args.length == 1) {
			sender.sendMessage(ChatColor.GREEN + "The reward limit for group " + group.getName() + " is currently " + group.getRewardLimit());
		} else { // args.length == 2
			if (!CMRPermission.LIMIT_MODIFY.attempt(sender)) return true;
			try {
				group.setRewardLimit(Integer.parseInt(args[1]));
				sender.sendMessage(ChatColor.GREEN + "Success!");
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Invalid number");
			}
		}
		return true;
	}

}
