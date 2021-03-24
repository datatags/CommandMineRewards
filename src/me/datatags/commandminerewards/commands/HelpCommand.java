package me.datatags.commandminerewards.commands;

import java.util.StringJoiner;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import me.datatags.commandminerewards.CMRPermission;

public class HelpCommand extends CMRCommand {
	private static final double MAX_PAGE_SIZE = 5; // is a double because int + int = int but int + double = double, and we need that for rounding.
	private static final String GENERIC_HELP = ChatColor.GREEN + "----------" + ChatColor.GOLD + "CommandMineRewards Help" + ChatColor.GREEN + "----------"; 
	@Override
	public String getName() {
		return "help";
	}
	@Override
	public String getBasicDescription() {
		return "See command list and descriptions";
	}

	@Override
	public String getExtensiveDescription() {
		return "Get help with commands. If no argument is specified, it will read page 1 of help. If a number argument is specified, it will read that page of the help. If a CMR command is specified, it will read the detailed help for that command (as you see now.)";
	}

	@Override
	public String getUsage() {
		return "[page|command] [subcommand]";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"block add"};
	}

	@Override
	public int getMaxArgs() {
		return 2;
	}
	
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.HELP;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		int page = 0;
		String command = null;
		if (args.length == 0) {
			page = 1;
		} else {
			try {
				page = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				command = args[0];
			}
		}
		CommandDispatcher cd = CommandDispatcher.getInstance();
		if (command == null) {
			int index = (int) ((page - 1) * MAX_PAGE_SIZE);
			double maxPage = Math.ceil(cd.getCommands().size() / MAX_PAGE_SIZE); 
			if (page > maxPage) {
				sender.sendMessage(ChatColor.RED + "There are only " + (int)maxPage + " pages, but you asked for page " + page);
				return true;
			}
			int stop = (int) Math.min(MAX_PAGE_SIZE + index, cd.getCommands().size());
			printHelpHeader(page, sender);
			if (sender instanceof ConsoleCommandSender) {
				stop = cd.getCommands().size(); // just dump all commands on console because they have a useful scroll function
			}
			for (int i = index; i < stop; i++) {
				CMRCommand cmd = cd.getCommands().get(i);
				sender.sendMessage(ChatColor.GOLD + "/cmr " + cmd.getName() + ": " + ChatColor.GREEN + cmd.getBasicDescription());
			}
			printHelpFooter(page, sender);
		} else {
			CMRCommand[] matches = cd.getCommands().stream().filter(c -> c.getName().equalsIgnoreCase(args[0])).toArray(CMRCommand[]::new);
			if (matches.length == 0) {
				sender.sendMessage(ChatColor.RED + "Unknown command: /cmr " + args[0] + ", try '/cmr help'.");
				return true;
			}
			CMRCommand cmd = matches[0];
			if (!cmd.getPermission().test(sender)) {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use that command, and therefore need no help with it.");
				return true;
			}
			sender.sendMessage(GENERIC_HELP);
			if (!(cmd instanceof CompoundCommand)) {
				printCommandHelp(cmd, "", sender);
				return true;
			}
			CompoundCommand ccmd = (CompoundCommand) cmd;
			if (args.length > 1) {
				CMRCommand[] submatches = ccmd.getChildren().stream().filter(c -> c.getName().equalsIgnoreCase(args[1])).toArray(CMRCommand[]::new);
				if (submatches.length == 0) {
					sender.sendMessage(ChatColor.RED + "Unknown command: /cmr " + cmd.getName() + " " + args[1] + ", try /cmr help " + cmd.getName());
					return true;
				}
				printCommandHelp(submatches[0], ccmd.getName() + " ", sender);
				return true;
			} else {
				StringJoiner names = new StringJoiner(", ");
				StringJoiner pipeSeparator = new StringJoiner("|", "<", ">");
				ccmd.getChildren().stream().forEach(c -> names.add(c.getName()));
				ccmd.getChildren().stream().forEach(c -> pipeSeparator.add(c.getName()));
				if (ccmd.getExtensiveDescription() != null) printCommandDescription(ccmd, sender);
				sender.sendMessage(ChatColor.GOLD + "Available subcommands: " + ChatColor.GREEN + names.toString() + ". Try '/cmr help <subcommand>' for more information.");
				sender.sendMessage(ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "/cmr " + cmd.getName() + " " + pipeSeparator.toString() + " ...");
				sender.sendMessage(GENERIC_HELP);
			}
		}
		return true;
	}
	private void printCommandDescription(CMRCommand cmd, CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "Description: " + ChatColor.GREEN + cmd.getExtensiveDescription());
	}
	private void printCommandHelp(CMRCommand cmd, String usagePrefix, CommandSender sender) {
		printCommandDescription(cmd, sender);
		sender.sendMessage(ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "/cmr " + usagePrefix + cmd.getName() + " " + cmd.getUsage());
		for (String example : cmd.getExamples()) {
			sender.sendMessage(ChatColor.GOLD + "Example: " + ChatColor.GREEN + "/cmr " + usagePrefix + cmd.getName() + " " + example);
		}
		sender.sendMessage(GENERIC_HELP);
	}
	private void printConsoleHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "CommandMineRewards Help" + ChatColor.GREEN + "----------");
	}
	private void printHelpHeader(int page, CommandSender sender) {
		if (sender instanceof ConsoleCommandSender) {
			printConsoleHelp(sender);
			return;
		}
		sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "CommandMineRewards" + (page == 1 ? "" : " - Page " + page) + ChatColor.GREEN + "----------");
	}
	private void printHelpFooter(int page, CommandSender sender) {
		if (sender instanceof ConsoleCommandSender) {
			printConsoleHelp(sender);
			return;
		}
		if (CommandDispatcher.getInstance().getCommands().size() / MAX_PAGE_SIZE <= page) { // if last page or past what was expected
			sender.sendMessage(ChatColor.GREEN + "----------------" + ChatColor.GOLD + "Page " + page + ChatColor.GREEN + "----------------");
		} else {
			sender.sendMessage(ChatColor.GREEN + "----------" + ChatColor.GOLD + "Page " + page + " - /cmr help " + (page + 1) + " for next page" + ChatColor.GREEN + "----------");
		}
	}
}
