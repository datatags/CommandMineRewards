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
		return "Plays the sound to the target. If you want to use a custom sound, prefix it with ! to bypass validity check. If used in-game, the target is the command sender. If used in a reward, the target is the reward recipient.";
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
		if (args[0].startsWith("!")) {
			try {
				sound = Sound.valueOf(args[0]);
			} catch (IllegalArgumentException e) {
				getPlugin().getLogger().warning("Couldn't find sound " + args[0] + ", if you are sure the sound works please prefix it with ! to disable this message.");
			}
		}
		if (sound == null) {
			target.playSound(target.getLocation(), args[0], 100, 0);
		} else {
			target.playSound(target.getLocation(), sound, 100, 0);
		}
		return true;
	}

}
