package me.datatags.commandminerewards.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.gui.GUIManager;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.guis.MainGUI;

public class GUICommand extends CMRCommand {
    private GUIManager gm = GUIManager.getInstance();
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
        return "Opens the CMR configuration GUI, or specify a player name to join their configuration session.";
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
        if (getPlugin().getMinecraftVersion() < 14) {
            sender.sendMessage(ChatColor.RED + "Sorry, the GUI is not available in 1.13 or legacy versions of minecraft. This may change as I get better stats on what minecraft versions are in use.");
            return true;
        }
        Player player = (Player) sender;
        GUIUserHolder holder = gm.getHolder(player);
        if (player.isConversing() && holder == null) {
            sender.sendMessage(ChatColor.RED + "Please exit any editors of other plugins before using the CMR GUI.");
            return true;
        }
        if (args.length == 1) {
            if (!CMRPermission.GUI_ASSIST.attempt(sender)) return true;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Invalid player: " + args[0]);
                return true;
            }
            gm.removeUser(player);
            GUIUserHolder targetHolder = gm.getHolder(target);
            if (targetHolder == null) {
                sender.sendMessage(ChatColor.RED + target.getName() + " is not configuring CMR at the moment.");
                return true;
            }
            targetHolder.addHelper(player);
            return true;
        }
        new MainGUI().openFor(player);
        return true;
    }

    @Override
    public CMRPermission getPermission() {
        return CMRPermission.GUI;
    }

}
