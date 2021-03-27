package me.datatags.commandminerewards.commands.special;

import java.util.StringJoiner;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.datatags.commandminerewards.CMRLogger;

public class MessageCommand extends SpecialCommand {
	// special commands are commands that aren't designed to be used in-game except for testing purposes
	@Override
	public String getName() {
		return "msg";
	}

	@Override
	public String getBasicDescription() {
		return "Special command, used in reward commands.";
	}

	@Override
	public String getExtensiveDescription() {
		return "Simply sends the specified message. Color codes are supported. PLEASE SEE /cmr help special FOR USAGE INFORMATION.";
	}
	
	@Override
	public String getUsage() {
		return "<message>";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"&aYou found a &8rock!"};
	}
	
	@Override
	public int getMinArgs() {
		return 1;
	}
	
	@Override
	public int getMaxArgs() {
		return -1;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (args.length == 0) {
			CMRLogger.warning(ChatColor.RED + "Command error: Not enough args in /cmr " + getName() + " " + args);
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
