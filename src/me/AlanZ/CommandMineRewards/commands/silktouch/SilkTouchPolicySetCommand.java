package me.AlanZ.CommandMineRewards.commands.silktouch;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.AlanZ.CommandMineRewards.GlobalConfigManager;
import me.AlanZ.CommandMineRewards.Reward;
import me.AlanZ.CommandMineRewards.RewardSection;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.AlanZ.CommandMineRewards.commands.ArgType;

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
		return "Sets the silk touch policy globally, per reward section, or per reward. This just indicates whether silk touch is required or prohibited on the tool used when receiving rewards.";
	}

	@Override
	public String getUsage() {
		return "<REQUIRED|IGNORED|DISALLOWED> [rewardSection] [reward]";
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
	public boolean isModifier() {
		return true;
	}
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		SilkTouchRequirement req = SilkTouchRequirement.getByName(args[0]);
		if (req == null) {
			sender.sendMessage(ChatColor.RED + "The policy " + args[0] + " was not understood.  Please put REQUIRED, IGNORED, OR DISALLOWED.");
			return true;
		}
		if (args.length == 1) {
			GlobalConfigManager.setGlobalSilkTouchRequirement(req);
			sender.sendMessage(SUCCESS);
			return true;
		}
		String rewardSection = args[1];
		if (args.length == 2) {
			try {
				new RewardSection(rewardSection).setSilkTouchRequirement(req);
			} catch (InvalidRewardSectionException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
		} else if (args.length == 3) {
			String reward = args[2];
			try {
				new Reward(rewardSection, reward).setSilkTouchRequirement(req);
			} catch (InvalidRewardSectionException | InvalidRewardException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
		}
		sender.sendMessage(SUCCESS);
		return true;
	}
}
