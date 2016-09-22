package me.AlanZ;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandMineRewards extends JavaPlugin {
	
	public Permission usePermission = new Permission("cmr.use");
	public Permission reloadPermission = new Permission("cmr.reload");
	public Permission multiplierPermission = new Permission("cmr.multiplier");
	
	double multiplier;
	boolean debug;
	List<String> blocks = new ArrayList<String>();
	
	@Override
	public void onEnable() {
		List<String> defBlocks = new ArrayList<String>();
		defBlocks.add("stone");
		defBlocks.add("cobblestone");
		defBlocks.add("mossy_cobblestone");
		this.getConfig().addDefault("Blocks", defBlocks);
		this.getConfig().addDefault("multiplier", 1);
		this.getConfig().addDefault("Rewards.1.chance", 5);
		this.getConfig().addDefault("Rewards.1.commands", new String[]{"token give %player% 10","eco give %player% 2000","broadcast %player% received rewards"});
		this.getConfig().addDefault("Rewards.2.chance", 20);
		this.getConfig().addDefault("Rewards.2.commands", new String[]{"eco give %player% 500"});
		this.getConfig().addDefault("debug", false);
		this.getConfig().options().copyDefaults(true);
		saveConfig();
		reload();
		if (debug) {getLogger().info("Blocks loading test:  " + blocks);}
		new BlockListener(this);
		
		getLogger().info("CommandMineRewards (by AlanZ) is enabled!");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("CommandMineRewards (by AlanZ) is being disabled!");
	}
	
	public void reload() {
		reloadConfig();
		blocks = this.getConfig().getStringList("Blocks");
		multiplier = this.getConfig().getDouble("multiplier");
		debug = this.getConfig().getBoolean("debug");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("cmr")) {
			
			if (args.length == 0) {
				sender.sendMessage(ChatColor.GREEN + "This server is running CommandMineRewards v1.0 by AlanZ");
			} else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission(reloadPermission)) {
					reload();
					sender.sendMessage(ChatColor.YELLOW + "Multiplier and list of blocks has been reloaded, list of rewards will be updated automatically.");
					if (debug) {
						sender.sendMessage(ChatColor.YELLOW + "Multiplier:  " + multiplier + ".  Blocks:  " + blocks + ".  Debug:  " + debug + ".");
						sender.sendMessage(ChatColor.YELLOW + "Multiplier:  " + this.getConfig().getDouble("multiplier") + ".  Blocks:  " + this.getConfig().getStringList("blocks") + ".  Debug:  " + this.getConfig().getBoolean("debug") + ".");
					}
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
							multiplier = Double.parseDouble(args[1]);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "Invalid number!");
							return false;
						}
						this.getConfig().set("multiplier", multiplier);
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
