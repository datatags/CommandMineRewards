package me.datatags.commandminerewards.commands.special;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.CMRLogger;

public class SoundCommand extends SpecialCommand {

    @Override
    public String getName() {
        return "sound";
    }

    @Override
    public String getBasicDescription() {
        return "Special command, used in reward commands.";
    }

    @Override
    public String getExtensiveDescription() {
        return "Plays the sound to the target. If you are using the latest MC server version, sounds are here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html, otherwise, sounds are here: https://helpch.at/docs/" + getPlugin().getFullMinecraftVersion() + "/org/bukkit/Sound.html  If you want to use a custom sound like from a resource pack, prefix it with ! to bypass validity check. \n" + ChatColor.RED + "PLEASE SEE /cmr help special FOR USAGE INFORMATION.";
    }
    
    @Override
    public String getUsage() {
        return "<sound> [volume] [pitch]";
    }
    
    @Override
    public String[] getExamples() {
        return new String[] {"BLOCK_ANVIL_LAND 100 2", "!mycoolresourcepack.winlottery"};
    }
    
    @Override
    public int getMinArgs() {
        return 1;
    }
    
    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used as a player!");
            return true;
        }
        Player target = (Player) sender;
        if (args.length < 1) {
            CMRLogger.warning("No sound argument supplied to sound command");
            return true;
        }
        Sound sound = null;
        float volume = 100;
        float pitch = 1;
        String soundString = args[0];
        boolean warn = true;
        if (soundString.startsWith("!")) {
            warn = false;
            soundString = soundString.substring(1);
        }
        try {
            sound = Sound.valueOf(soundString.toUpperCase());
        } catch (IllegalArgumentException e) {
            if (warn) CMRLogger.warning("Couldn't find sound " + soundString + ", if you are sure the sound works please prefix it with ! to disable this message.");
        }
        if (args.length > 1) {
            try {
                volume = Float.parseFloat(args[1]);
            } catch (NumberFormatException e) {
                CMRLogger.warning("Invalid volume: " + args[1]);
                return true;
            }
            if (args.length > 2) {
                try {
                    pitch = Float.parseFloat(args[2]);
                } catch (NumberFormatException e) {
                    CMRLogger.warning("Invalid pitch: " + args[2]);
                    return true;
                }
            }
        }
        if (sound == null) {
            target.playSound(target.getLocation(), args[0], volume, pitch);
        } else {
            target.playSound(target.getLocation(), sound, volume, pitch);
        }
        return true;
    }

}
