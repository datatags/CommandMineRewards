package me.datatags.commandminerewards.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.CMRPermission;

public class DebugCommand extends CMRCommand {

	@Override
	public String getName() {
		return "debug";
	}

	@Override
	public String getBasicDescription() {
		return "Switches CMR into debug mode.";
	}

	@Override
	public String getExtensiveDescription() {
		return "Sets verbosity:2 and enables debug logging (debug.log in CMR directory) but only until next server restart.";
	}

	@Override
	public String[] getExamples() {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (CMRLogger.toggleDebugMode()) { // returns new debug mode value
			sender.sendMessage(ChatColor.YELLOW + "Debug logging is now enabled, you can find it here: " + CMRLogger.getDebugLogPath());
		} else {
			sender.sendMessage(ChatColor.YELLOW + "Debug logging is now disabled.");
		}
		return true;
	}

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.ADMIN;
	}
	
}
