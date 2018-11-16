package me.AlanZ.CommandMineRewards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.permissions.Permission;
import org.bukkit.util.StringUtil;

public class CMRTabComplete implements TabCompleter {
	private Map<String,List<ArgType>> commandArgs = new HashMap<String,List<ArgType>>();
	private Map<String,Permission> commandPermissions;
	CommandMineRewards cmr;
	CMRTabComplete(CommandMineRewards cmr, Map<String,Permission> cmdPermissions) {
		this.cmr = cmr;
		this.commandPermissions = cmdPermissions;
		// command argument info
		addCommand("reload", ArgType.NONE);
		addCommand("multiplier", ArgType.NONE);
		addCommand("help", ArgType.NONE);
		addCommand("addblock", ArgType.REWARD_SECTION, ArgType.BLOCK);
		addCommand("removeblock", ArgType.REWARD_SECTION, ArgType.BLOCK);
		addCommand("listblocks", ArgType.REWARD_SECTION);
		addCommand("addreward", ArgType.REWARD_SECTION);
		addCommand("removereward", ArgType.REWARD_SECTION, ArgType.REWARD);
		addCommand("listrewards", ArgType.REWARD_SECTION);
		addCommand("addcommand", ArgType.REWARD_SECTION, ArgType.REWARD, ArgType.NONE);
		addCommand("insertcommand", ArgType.REWARD_SECTION, ArgType.REWARD, ArgType.NONE, ArgType.NONE);
		addCommand("removecommand", ArgType.REWARD_SECTION, ArgType.REWARD, ArgType.NONE);
		addCommand("listcommands", ArgType.REWARD_SECTION, ArgType.REWARD);
		addCommand("addworld", ArgType.WORLD_OR_SECTION, ArgType.WORLD);
		addCommand("addcurrentworld", ArgType.REWARD_SECTION);
		addCommand("removeworld", ArgType.WORLD_OR_SECTION, ArgType.WORLD);
		addCommand("removecurrentworld", ArgType.REWARD_SECTION);
		addCommand("listworlds", ArgType.REWARD_SECTION);
		addCommand("addregion", ArgType.REGION_OR_SECTION, ArgType.REGION);
		addCommand("removeregion", ArgType.REGION_OR_SECTION, ArgType.REGION);
		addCommand("listregions", ArgType.REWARD_SECTION);
		addCommand("setsilktouchpolicy", ArgType.SILKTOUCH_OR_SECTION, ArgType.SILKTOUCH_OR_REWARD, ArgType.SILKTOUCH);
		addCommand("viewsilktouchpolicy", ArgType.REWARD_SECTION, ArgType.REWARD);
		addCommand("chance", ArgType.REWARD_SECTION, ArgType.REWARD, ArgType.NONE);
	}
	private void addCommand(String command, ArgType... argTypes) {
		List<ArgType> args = new ArrayList<ArgType>(Arrays.asList(argTypes));
		commandArgs.put(command, args);
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		List<String> options = new ArrayList<String>();
		if (args.length < 1) {
			return options; // what
		} else if (args.length == 1) {
			//options.addAll(commandArgs.keySet());
			for (Entry<String,Permission> entry : commandPermissions.entrySet()) {
				if (sender.hasPermission(entry.getValue())) {
					options.add(entry.getKey());
				}
			}
		} else {
			if (!commandArgs.containsKey(args[0])) { // UPDATE: I was lied to.  It does not contain the label. Original: should theoretically be args[1] for subcommand because it says it includes the command label
				// debug message cmr.getLogger().warning("Could not find CMR sub-command named " + args[0]); // if we guessed wrong, say so
				return options; // return empty
			}
			List<ArgType> chosenArgs = commandArgs.get(args[0]);
			if (args.length - 1 > chosenArgs.size()) {
				return options; // no more args we know of
			}
			ArgType currentArg = chosenArgs.get(args.length - 2); // last arg should be x - 1 because we don't want to count the sub-command
			if (currentArg == ArgType.NONE) {
				return options; // return empty because there's nothing (we need to know about) here
			}
			if (currentArg == ArgType.BLOCK) {
				return options; // TODO: Return list of blocks rather than nothing.
			}
			if (currentArg == ArgType.REWARD_SECTION || currentArg == ArgType.REGION_OR_SECTION || currentArg == ArgType.SILKTOUCH_OR_SECTION || currentArg == ArgType.WORLD_OR_SECTION) {
				options.addAll(GlobalConfigManager.getRewardSectionNames());
			}
			if (currentArg == ArgType.REWARD || currentArg == ArgType.SILKTOUCH_OR_REWARD) {
				String sectionName = args[args.length - 2]; // not the last one, the incomplete one, but the second-to-last one that has the section in it.  I hope this works...
				options.addAll(new RewardSection(sectionName).getChildrenNames());
			}
			if (currentArg == ArgType.REGION || currentArg == ArgType.REGION_OR_SECTION) {
				if (cmr.usingWorldGuard()) {
					options.addAll(WorldGuardManager.getAllRegions());
				}
			}
			if (currentArg == ArgType.WORLD || currentArg == ArgType.WORLD_OR_SECTION) {
				for (World world : Bukkit.getServer().getWorlds()) {
					options.add(world.getName());
				}
			}
			if (currentArg == ArgType.SILKTOUCH || currentArg == ArgType.SILKTOUCH_OR_REWARD || currentArg == ArgType.SILKTOUCH_OR_SECTION) {
				for (SilkTouchRequirement str : SilkTouchRequirement.values()) {
					options.add(str.toString()); // forgot the pretty names include color codes which screws with commands pretty badly
				}
			}
		}
		List<String> matches = new ArrayList<String>();
		StringUtil.copyPartialMatches(args[args.length - 1], options, matches);
		return matches;
	}

}
