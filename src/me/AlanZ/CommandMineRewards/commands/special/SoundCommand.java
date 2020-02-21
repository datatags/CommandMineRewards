package me.AlanZ.CommandMineRewards.commands.special;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
		return "Plays the sound to the target. Available sounds in latest: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html, legacy sounds at https://helpch.at/docs/" + getPlugin().getFullMinecraftVersion() + "/org/bukkit/Sound.html If you want to use a custom sound, prefix it with ! to bypass validity check. If used in-game, the target is the command sender. If used in a reward, the target is the reward recipient.";
	}
	
	@Override
	public String getUsage() {
		return "<sound>";
	}
	
	@Override
	public boolean isModifier() {
		return false;
	}
	
	@Override
	public int getMinArgs() {
		return 1;
	}
	
	@Override
	public int getMaxArgs() {
		return 1;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be used as a player!");
			return true;
		}
		Player target = (Player) sender;
		if (args.length < 1) {
			getPlugin().getLogger().warning("No sound argument supplied to sound command");
			return true;
		}
		Sound sound = null;
		String soundString = args[0];
		boolean warn = true;
		if (soundString.startsWith("!")) {
			warn = false;
			soundString = soundString.substring(1);
		}
		try {
			sound = Sound.valueOf(soundString);
		} catch (IllegalArgumentException e) {
			if (warn) getPlugin().getLogger().warning("Couldn't find sound " + soundString + ", if you are sure the sound works please prefix it with ! to disable this message.");
		}
		if (sound == null) {
			target.playSound(target.getLocation(), args[0], 100, 0);
		} else {
			target.playSound(target.getLocation(), sound, 100, 0);
		}
		return true;
	}

}
