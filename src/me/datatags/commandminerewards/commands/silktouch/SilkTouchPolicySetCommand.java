package me.datatags.commandminerewards.commands.silktouch;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.SilkTouchPolicy;
import me.datatags.commandminerewards.Exceptions.InvalidRewardException;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.commands.ArgType;

public class SilkTouchPolicySetCommand extends SilkTouchPolicyCommand {
	@Override
	public String getName() {
		return "set";
	}
	@Override
	public String getBasicDescription() {
		return "Sets the silk touch policy.";
	}

	@Override
	public String getExtensiveDescription() {
		return "Sets the silk touch policy globally, per reward group, or per reward. This just indicates whether silk touch is required or prohibited on the tool used when receiving rewards.";
	}

	@Override
	public String getUsage() {
		return "<REQUIRED|IGNORED|DISALLOWED> [rewardSection] [reward]";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"DISALLOWED", "IGNORED genericRewards bigReward"};
	}

	@Override
	public int getMinArgs() {
		return 1;
	}

	@Override
	public int getMaxArgs() {
		return 3;
	}

	@Override
	public ArgType[] getArgs() {
		return new ArgType[] {ArgType.SILKTOUCH, ArgType.REWARD_SECTION, ArgType.REWARD};
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.SILKTOUCHPOLICY_MODIFY;
	}
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		SilkTouchPolicy req = SilkTouchPolicy.getByName(args[0]);
		if (req == null) {
			sender.sendMessage(ChatColor.RED + "The policy " + args[0] + " was not understood.  Please put REQUIRED, IGNORED, OR DISALLOWED.");
			return true;
		}
		if (args.length == 1) {
			GlobalConfigManager.getInstance().setGlobalSilkTouchPolicy(req);
			sender.sendMessage(SUCCESS);
			return true;
		}
		String rewardSection = args[1];
		if (args.length == 2) {
			try {
				new RewardGroup(rewardSection).setSilkTouchPolicy(req);
			} catch (InvalidRewardGroupException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
		} else if (args.length == 3) {
			String reward = args[2];
			try {
				new Reward(rewardSection, reward).setSilkTouchPolicy(req);
			} catch (InvalidRewardGroupException | InvalidRewardException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
		}
		sender.sendMessage(SUCCESS);
		return true;
	}
}
