package me.AlanZ.CommandMineRewards.commands.special;

import java.util.StringJoiner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MessageCommand implements CommandExecutor {
	// CommandMineRewards.commands.special does not extend CMR commands because they are for reward usage only
	// return value is failure reason, null if no failure
	// as of 6.1, these are registered as real commands beginning with c
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Command error: Not enough args in " + label + " " + args);
			return true;
		}
		StringJoiner message = new StringJoiner(" ");
		for (String arg : args) {
			message.add(arg);
		}
		String colorized = ChatColor.translateAlternateColorCodes('&', message.toString());
		sender.sendMessage(colorized);
		return true;
	}
}
