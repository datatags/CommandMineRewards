package me.Datatags.CommandMineRewards.commands.special;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TitleCommand extends SpecialCommand {
	@Override
	public String getName() {
		return "title";
	}

	@Override
	public String getBasicDescription() {
		return "Special command, used in reward commands.";
	}

	@Override
	public String getExtensiveDescription() {
		return "Sends a title. You must enter both a title and a subtitle or put 'none' for whichever one you don't want to use.  Use underscores instead of spaces. This command is not supported in 1.8 or below and setting fadeIn/stay/fadeOut isn't supported in 1.10 or below. PLEASE SEE /cmr help special FOR USAGE INFORMATION.";
	}
	
	@Override
	public String getUsage() {
		return "<title> <subtitle> [<fadeIn> <stay> <fadeOut>]";
	}
	
	@Override
	public String[] getExamples() {
		return new String[] {"&aGood_job! &bYou_win! 20 60 20", "none &cEh,_whatever"};
	}
	
	@Override
	public int getMinArgs() {
		return 2;
	}
	
	@Override
	public int getMaxArgs() {
		return 5;
	}

	@SuppressWarnings("deprecation") // because sendTitle without fadeIn/stay/fadeOut is "subject to change" but we need it sometimes
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
			return true;
		}
		Player target = (Player) sender;
		if (args.length != 5 && args.length != 2) {
			getPlugin().warning("Incorrect number of args: " + args.length);
			return true;
		}
		if (getPlugin().getMinecraftVersion() < 9) {
			getPlugin().warning("This version of minecraft does not support the !title command.");
			return true;
		}
		if (getPlugin().getMinecraftVersion() < 11 && args.length == 5) {
			getPlugin().warning("This version of minecraft does not support setting the fadeIn/stay/fadeOut for titles, but you can still use it without those.");
			return true;
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
				getPlugin().warning("Couldn't run command title: Couldn't parse one of: " + args[2] + ", " + args[3] + ", " + args[4] + " as number");
				return true;
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
		if (getPlugin().getMinecraftVersion() < 11) {
			// if we don't have the featureful method, use the old, deprecated one
			target.sendTitle(title, subtitle);
		} else {
			target.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		}
		return true;
	}
}
