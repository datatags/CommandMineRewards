package me.AlanZ.CommandMineRewards.commands.world;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.AlanZ.CommandMineRewards.GlobalConfigManager;
import me.AlanZ.CommandMineRewards.RewardSection;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.AlanZ.CommandMineRewards.Exceptions.InvalidWorldException;
import me.AlanZ.CommandMineRewards.Exceptions.WorldAlreadyInListException;
import me.AlanZ.CommandMineRewards.commands.ArgType;

public class WorldAddCommand extends WorldCommand {
	@Override
	public String getName() {
		return "add";
	}
	@Override
	public String getBasicDescription() {
		return "Adds an allowed world";
	}

	@Override
	public String getExtensiveDescription() {
		return "Adds a world in which rewards under a reward section, or globally, are allowed to occur. If an allowed world list is specified in a reward section it overrides the global one. Adding no arguments will add your current world to the global list if you are a player.";
	}

	@Override
	public String getUsage() {
		return "[world] [rewardSection]";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"", "survival cropRewards"};
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
		return new ArgType[] {ArgType.WORLD, ArgType.REWARD_SECTION};
	}
	@Override
	public boolean isModifier() {
		return true;
	}
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		GlobalConfigManager gcm = GlobalConfigManager.getInstance();
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Please specify a world to add.");
				return false;
			}
			Player player = (Player)sender;
			try {
				gcm.addGlobalAllowedWorld(player.getWorld().getName());
			} catch (WorldAlreadyInListException | InvalidWorldException e) {
				sender.sendMessage(e.getMessage());
			}
			return true;
		}
		String world = args[0];
		if (args.length == 1) {
			try {
				gcm.addGlobalAllowedWorld(world);
			} catch (WorldAlreadyInListException | InvalidWorldException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			sender.sendMessage(SUCCESS);
		} else if (args.length == 2) {
			String rewardSection = args[1];
			try {
				new RewardSection(rewardSection).addAllowedWorld(world);
			} catch (InvalidRewardSectionException | WorldAlreadyInListException | InvalidWorldException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
			sender.sendMessage(SUCCESS);
		}
		return true;
	}
}
