package me.Datatags.CommandMineRewards.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import me.Datatags.CommandMineRewards.CommandMineRewards;

public abstract class CMRCommand {
	public static final String NO_PERMISSION = ChatColor.RED + "You do not have permission to use this command!";
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
	public abstract boolean isModifier(); // should return true for any command that requires the cmr.<command>.modify permission to use
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
	
	public Permission getPermission() {
		return getPermission(isModifier());
	}
	public Permission getPermission(boolean modify) {
		return new Permission("cmr." + getName() + (modify ? ".modify" : ""));
	}
	protected CommandMineRewards getPlugin() {
		return (CommandMineRewards) Bukkit.getPluginManager().getPlugin("CommandMineRewards");
	}
	public void init() {
		// Compound commands need this, direct subcommands don't really
	}
}
