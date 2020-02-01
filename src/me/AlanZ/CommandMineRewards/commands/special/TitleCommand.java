package me.AlanZ.CommandMineRewards.commands.special;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TitleCommand {
	public static String onCommand(Player target, String[] args) {
		if (args.length != 5 && args.length != 2) {
			return "Wrong number of args";
		}
		String title = args[0];
		String subtitle = args[1];
		int fadeIn;
		int stay;
		int fadeOut;
		if (args.length == 2) {
			fadeIn = stay = fadeOut = -1;
		} else {
			try {
				fadeIn = Integer.parseInt(args[2]);
				stay = Integer.parseInt(args[3]);
				fadeOut = Integer.parseInt(args[4]);
			} catch (NumberFormatException e) {
				return "Couldn't parse one of: " + args[2] + ", " + args[3] + ", " + args[4] + " as number";
			}
		}
		title = ChatColor.translateAlternateColorCodes('&', title.replace('_', ' '));
		subtitle = ChatColor.translateAlternateColorCodes('&', subtitle.replace('_', ' '));
		if (title.equals("none")) {
			title = "";
		}
		if (subtitle.equals("none")) {
			subtitle = "";
		}
		target.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		return null;
	}
}
