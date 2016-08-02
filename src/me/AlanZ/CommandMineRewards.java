package me.AlanZ;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandMineRewards extends JavaPlugin {
	
	public Permission usePermission = new Permission("cmr.use");
	public Permission reloadPermission = new Permission("cmr.reload");
	public Permission multiplierPermission = new Permission("cmr.multiplier");
	
	int multiplier;
	
	FileConfiguration config = this.getConfig();
	List<String> blocks = new ArrayList<String>();
	List<String> commands1 = new ArrayList<String>();
	List<String> commands2 = new ArrayList<String>();
	
	@Override
	public void onEnable() {
		
		getConfig().options().copyDefaults(true);
		config.addDefault("Blocks", new String[]{"stone","cobblestone","mossy_cobblestone"});
		config.addDefault("multiplier", 1);
		config.addDefault("Rewards.1.chance", 20);
		config.addDefault("Rewards.1.commands", new String[]{"token give %player% 10","eco give %player% 2000","broadcast %player% received rewards"});
		config.addDefault("Rewards.2.chance", 20);
		config.addDefault("Rewards.2.commands", new String[]{"eco give %player% 500"});
		saveDefaultConfig();
		reload();
		
		new BlockListener(this);
		
		getLogger().info("CommandMineRewards (by AlanZ) is enabled!");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("CommandMineRewards (by AlanZ) is being disabled!");
	}
	
	public void reload() {
		blocks = config.getStringList("Blocks");
		multiplier = config.getInt("multiplier");
	}
	
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("cmr")) {
			
			if (args.length == 0) {
				sender.sendMessage(ChatColor.GREEN + "This server is running CommandMineRewards v1.0 by AlanZ");
			} else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission(reloadPermission)) {
					reload();
					sender.sendMessage(ChatColor.YELLOW + "Multiplier and list of blocks has been reloaded, list of rewards will be updated automatically.");
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have permission to reload the config!");
				}
			} else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.GOLD + "CommandMineRewards");
				sender.sendMessage(ChatColor.GOLD + "/cmr: Displays information about the plugin.");
				sender.sendMessage(ChatColor.GOLD + "/cmr reload: Reload the config.");
				sender.sendMessage(ChatColor.GOLD + "/cmr multiplier [multiplier]: Change the reward chance multiplier.");
				sender.sendMessage(ChatColor.GOLD + "/cmr help: Display this message.");
			} else if (args[0].equalsIgnoreCase("multiplier")) {
				if (sender.hasPermission(multiplierPermission)) {
					if (args.length == 2) {
						try {
							multiplier = Integer.parseInt(args[1]);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "Invalid integer!");
							return false;
						}
						config.set("multiplier", multiplier);
						saveConfig();
						sender.sendMessage(ChatColor.GREEN + "Multiplier successfully updated!  New multiplier:  " + multiplier);
					} else if (args.length == 1) {
						sender.sendMessage(ChatColor.YELLOW + "Current multiplier:  " + multiplier);
					} else {
						sender.sendMessage(ChatColor.RED + "Incorrect usage! ");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use the multiplier command!");
				}
			}
			return true;
		}
		
		return false;	
		
	}
}
