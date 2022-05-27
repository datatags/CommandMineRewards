package me.datatags.commandminerewards.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.datatags.commandminerewards.CMRPermission;

public class ReloadCommand extends CMRCommand {
    @Override
    public String getName() {
        return "reload";
    }
    @Override
    public String getBasicDescription() {
        return "Reload the config";
    }

    @Override
    public String getExtensiveDescription() {
        return "Simply reloads the CMR config.  All options, rewards, and sections will be reloaded.  All block lists will be re-checked for invalid blocks.";
    }
    
    @Override
    public String[] getExamples() {
        return null;
    }
    
    @Override
    public CMRPermission getPermission() {
        return CMRPermission.RELOAD;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        getPlugin().reload();
        sender.sendMessage(ChatColor.GREEN + "CMR config reloaded!");
        return true;
    }

}
