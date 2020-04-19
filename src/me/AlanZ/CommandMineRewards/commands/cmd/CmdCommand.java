package me.AlanZ.CommandMineRewards.commands.cmd;

import me.AlanZ.CommandMineRewards.commands.CompoundCommand;

public class CmdCommand extends CompoundCommand {
	// didn't really want to name the class "CommandCommand" so it's CmdCommand despite the actual command name being "command"
	@Override
	public String getName() {
		return "command";
	}
	
	@Override
	public String getBasicDescription() {
		return "Add, list, or remove commands.";
	}
	
	protected String parseCommand(int startIndex, String[] args) {
		String command = "";
		for (int i = startIndex; i < args.length; i++) {
			command += args[i] + " ";
		}
		if (command.startsWith("/")) {
			command = command.substring(1, command.length() - 1); // remove slash and trailing space
		} else {
			command = command.substring(0, command.length() - 1); // remove trailing space
		}
		return command;
	}
	@Override
	public void init() {
		registerChildren(new CmdAddCommand(), new CmdInsertCommand(), new CmdListCommand(), new CmdRemoveCommand(), new CmdReplaceCommand());
	}
}
