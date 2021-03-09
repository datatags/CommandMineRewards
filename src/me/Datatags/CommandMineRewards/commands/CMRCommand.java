package me.Datatags.CommandMineRewards.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.CommandMineRewards;

public abstract class CMRCommand {
	public static final String INTERNAL_ERROR = ChatColor.RED + "An internal error has occurred.  Please ask an admin to check the log.";
	public static final String SUCCESS = ChatColor.GREEN + "Success!";
	public CMRCommand() {
		// only need to register top-level commands so do it by command
	}
	public abstract String getName();
	public abstract String getBasicDescription();
	public abstract String getExtensiveDescription();
	public String getUsage() {
		return "";
	}
	public abstract String[] getExamples();
	public int getMinArgs() {
		return 0;
	}
	public int getMaxArgs() {
		return 0;
	}
	public ArgType[] getArgs() {
		return new ArgType[] {};
	}
	public String[] getAliases() {
		return new String[] {};
	}
	public abstract boolean onCommand(CommandSender sender, String[] args);
	public abstract CMRPermission getPermission();
	protected CommandMineRewards getPlugin() {
		return (CommandMineRewards) Bukkit.getPluginManager().getPlugin("CommandMineRewards");
	}
	public void init() {
		// Compound commands need this, direct subcommands don't really
	}
}
