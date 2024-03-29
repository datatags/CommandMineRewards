package me.datatags.commandminerewards.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.GlobalConfigManager;

public class MultiplierCommand extends CMRCommand {
    @Override
    public String getName() {
        return "multiplier";
    }
    @Override
    public String getBasicDescription() {
        return "Change the reward chance multiplier";
    }

    @Override
    public String getExtensiveDescription() {
        return "If a chance argument is specified, update the chance of the specified reward. Otherwise, read the chance of the specified reward.";
    }

    @Override
    public String getUsage() {
        return "[multiplier]";
    }

    @Override
    public String[] getExamples() {
        return new String[] {"3.0", "0.01"};
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public CMRPermission getPermission() {
        return CMRPermission.MULTIPLIER;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        GlobalConfigManager gcm = GlobalConfigManager.getInstance();
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Current multiplier:  " + gcm.getMultiplier());
            return true;
        }
        // args.length == 1 unless something went wrong and we don't care anyway so
        if (!CMRPermission.MULTIPLIER_MODIFY.attempt(sender)) return true;
        double multiplier;
        try {
            multiplier = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + args[0] + " is not a valid number!");
            return true;
        }
        gcm.setMultiplier(multiplier); // does bounds checking on its own
        sender.sendMessage(ChatColor.GREEN + "Multiplier successfully updated! New multiplier: " + multiplier);
        return true;
    }

}
