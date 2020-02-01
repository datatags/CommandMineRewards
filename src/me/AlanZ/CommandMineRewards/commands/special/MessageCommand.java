package me.AlanZ.CommandMineRewards.commands.special;

import java.util.StringJoiner;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class MessageCommand {
	// CommandMineRewards.commands.special does not extend CMR commands because they are for reward usage only
	// return value is failure reason, null if no failure
	public static String onCommand(Player target, String[] args) {
		if (args.length == 0) {
			return "Not enough args";
		}
		StringJoiner message = new StringJoiner(" ");
		for (String arg : args) {
			message.add(arg);
		}
		String colorized = ChatColor.translateAlternateColorCodes('&', message.toString());
		target.sendMessage(colorized);
		return null;
	}
}
