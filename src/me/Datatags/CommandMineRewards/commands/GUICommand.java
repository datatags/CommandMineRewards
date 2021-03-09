package me.Datatags.CommandMineRewards.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.gui.GUIManager;
import me.Datatags.CommandMineRewards.gui.guis.MainGUI;

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
		return "Opens the CMR configuration GUI";
	}

	@Override
	public String[] getExamples() {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You can't use the GUI unless you're a player!");
			return true;
		}
		// TODO: open last GUI player was using instead of the main one every time
		GUIManager.getInstance().getGUI(MainGUI.class, null, null).openFor((Player)sender);
		return true;
	}

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.GUI;
	}

}
