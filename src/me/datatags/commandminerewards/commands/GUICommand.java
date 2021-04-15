package me.datatags.commandminerewards.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.guis.CMRGUI;
import me.datatags.commandminerewards.gui.guis.MainGUI;

public class GUICommand extends CMRCommand {

	@Override
	public String getName() {
		return "gui";
	}

	@Override
	public String getBasicDescription() {
		return "Opens the CMR GUI";
	}

	@Override
	public String getExtensiveDescription() {
		return "Opens the CMR configuration GUI. Specify a player name to join their configuration session if you are an admin.";
	}
	
	@Override
	public String getUsage() {
		return "[player]";
	}

	@Override
	public String[] getExamples() {
		return new String[] {"", "PlayerBob"};
	}
	
	@Override
	public int getMaxArgs() {
		return 1;
	}
	
	@Override
	public ArgType[] getArgs() {
		return new ArgType[] {ArgType.PLAYER};
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You can't use the GUI unless you're a player!");
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 1) {
			if (!CMRPermission.GUI_ASSIST.attempt(sender)) return true;
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "Invalid player: " + args[0]);
				return true;
			}
			GUIUserHolder holder = CMRGUI.getHolder(target);
			if (holder == null) {
				sender.sendMessage(ChatColor.RED + target.getName() + " is not configuring CMR at the moment.");
				return true;
			}
			holder.addHelper(player);
			return true;
		}
		// TODO: open last GUI player was using instead of the main one every time?
		new MainGUI().openFor(player);
		return true;
	}

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.GUI;
	}

}
