package me.Datatags.CommandMineRewards.commands.special;

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
		return "Plays the sound to the target. If you are using the latest MC server version, sounds are here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html, otherwise, sounds are here: https://helpch.at/docs/" + getPlugin().getFullMinecraftVersion() + "/org/bukkit/Sound.html  If you want to use a custom sound like from a resource pack, prefix it with ! to bypass validity check. PLEASE SEE /cmr help special FOR USAGE INFORMATION.";
	}
	
	@Override
	public String getUsage() {
		return "<sound>";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"BLOCK_ANVIL_LAND", "!mycoolresourcepack.winlottery"};
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
			getPlugin().warning("No sound argument supplied to sound command");
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
			if (warn) getPlugin().warning("Couldn't find sound " + soundString + ", if you are sure the sound works please prefix it with ! to disable this message.");
		}
		if (sound == null) {
			target.playSound(target.getLocation(), args[0], 100, 0);
		} else {
			target.playSound(target.getLocation(), sound, 100, 0);
		}
		return true;
	}

}
