package me.Datatags.CommandMineRewards.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import me.Datatags.CommandMineRewards.CommandMineRewards;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.Datatags.CommandMineRewards.commands.silktouch.SilkTouchRequirement;
import me.Datatags.CommandMineRewards.worldguard.WorldGuardManager;

public class CMRTabComplete implements TabCompleter {
	private CommandMineRewards cmr;
	public CMRTabComplete(CommandMineRewards cmr) {
		this.cmr = cmr;
		cmr.getCommand("cmr").setTabCompleter(this);
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		CommandDispatcher cd = CommandDispatcher.getInstance();
		List<String> options = new ArrayList<String>();
		if (args.length < 1) { // this shouldn't ever happen but handle it anyway
			return options;
		} else if (args.length == 1 || (args.length == 2 && args[0].equalsIgnoreCase("help"))) {
			for (CMRCommand item : cd.getCommands()) {
				if (sender.hasPermission(item.getPermission())) { // if value is null, there is no permission for that command
					options.add(item.getName());
					String[] aliases = item.getAliases();
					if (aliases != null && aliases.length > 0) {
						options.addAll(Arrays.asList(aliases));
					}
				}
			}
		} else {
			CMRCommand command = cd.getCommand(args[0]);
			if (command == null) {
				return options; // return empty
			}
			COMPOUNDARGS: { 
				if ((command instanceof CompoundCommand && args.length == 2) || (command.getName().equals("help") && args.length == 3)) {
					CompoundCommand ccmd;
					if (command.getName().equals("help")) {
						if (!(cd.getCommand(args[1]) instanceof CompoundCommand)) {
							break COMPOUNDARGS;
						}
						ccmd = (CompoundCommand) cd.getCommand(args[1]);
					} else {
						ccmd = (CompoundCommand) command;
					}
					for (CompoundCommand child : ccmd.getChildren()) {
						if (sender.hasPermission(child.getPermission())) {
							options.add(child.getName());
						}
					}
					return processMatches(args, options);
				}
			}
			CMRCommand workingCommand;
			int compoundOffset;
			if (command instanceof CompoundCommand) {
				CMRCommand[] matches = ((CompoundCommand) command).getChildren().stream().filter(c -> c.getName().equalsIgnoreCase(args[1])).toArray(CMRCommand[]::new);
				if (matches.length == 0) {
					return options;
				}
				workingCommand = matches[0];
				compoundOffset = 1;
			} else {
				workingCommand = command;
				compoundOffset = 0;
			}
			ArgType[] chosenArgs = workingCommand.getArgs();
			if (chosenArgs == null || args.length - (1 + compoundOffset) > chosenArgs.length) {
				return options; // no more args we know of
			}
			if (args[0].equalsIgnoreCase("help")) {
				for (CMRCommand item : cd.getCommands()) {
					if (sender.hasPermission(item.getPermission())) {
						options.add(item.getName());
					}
				}
				return processMatches(args, options);
			}
			ArgType currentArg = chosenArgs[args.length - (2 + compoundOffset)]; // last arg should be x - 1 because we don't want to count the sub-command
			if (currentArg == ArgType.BLOCK) {
				return options; // TODO: Return list of blocks rather than nothing?
			}
			if (currentArg == ArgType.REWARD_SECTION) {
				options.addAll(GlobalConfigManager.getInstance().getRewardSectionNames());
			}
			if (currentArg == ArgType.REWARD) {
				String sectionName = args[args.length - 2]; // not the last one, the incomplete one, but the second-to-last one that has the section in it.  I hope this works...
				try {
				options.addAll(new RewardSection(sectionName).getChildrenNames());
				} catch (InvalidRewardSectionException e) {
					return options;
				}
			}
			if (currentArg == ArgType.REGION) {
				WorldGuardManager wgm = cmr.getWGManager();
				if (wgm.usingWorldGuard()) {
					options.addAll(wgm.getAllRegions());
				}
			}
			if (currentArg == ArgType.WORLD) {
				for (World world : Bukkit.getServer().getWorlds()) {
					options.add(world.getName());
				}
			}
			if (currentArg == ArgType.SILKTOUCH) {
				for (SilkTouchRequirement str : SilkTouchRequirement.values()) {
					options.add(str.toString()); // forgot the pretty names include color codes which screws with commands pretty badly
				}
			}
		}
		return processMatches(args, options);
	}
	private List<String> processMatches(String[] args, List<String> options) {
		List<String> matches = new ArrayList<String>();
		StringUtil.copyPartialMatches(args[args.length - 1], options, matches);
		return matches;
	}

}
