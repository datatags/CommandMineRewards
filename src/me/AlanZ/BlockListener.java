package me.AlanZ;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {
	
	CommandMineRewards that;
	public BlockListener(CommandMineRewards plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);	
		this.that = plugin;
	}
	
	@EventHandler
	public void onEvent(BlockBreakEvent e) {
		if (that.debug) {that.getLogger().info("----------START OF REWARD CALCS--------");}
		if (that.debug) {that.getLogger().info("Blocks list:  " + that.getConfig().getStringList("Blocks"));}
		Player player = e.getPlayer();
		GameMode gm = player.getGameMode();
			boolean blockFound = false; 
			for (String block : that.blocks) {
				if(e.getBlock().getType() == Material.valueOf(block.toUpperCase())) {
					blockFound = true;
					break;
				}
			}
			if (blockFound) {
				// No longer needed   int rewardCount = (new HashSet<String>(that.getConfig().getConfigurationSection("Rewards").getKeys(false))).size();
				List<Double> rewardChances = new ArrayList<Double>();
				List<String> activeRewards = new ArrayList<String>();
				for (String temp : that.getConfig().getConfigurationSection("Rewards").getKeys(false)) {	
					if (player.hasPermission("cmr.use." + temp) || player.hasPermission("cmr.use.*")) { //This big mess means add the "selected" reward, the one contained in "temp", (as long as, if enabled, the player is in survival mode,) AND (the player has either the permission cmr.use.(reward name stored in temp) OR the player has cmr.use)
						if ((that.getConfig().getBoolean("Rewards." + temp + ".survivalOnly") && gm == GameMode.SURVIVAL) || !that.getConfig().getBoolean("Rewards." + temp + ".survivalOnly")) {
							activeRewards.add(temp);
							rewardChances.add((that.getConfig().getDouble("Rewards." + temp + ".chance") * that.multiplier));
						} else if (that.debug){
							that.getLogger().info("Player " + player.getName() + " did not receive reward " + temp + " because they were in the wrong game mode!");
						}
					} else if (that.debug) {
						that.getLogger().info("Player " + player.getName() + " did not receive reward " + temp + " for lack of permission!");
					}
				}
				if(that.debug){that.getLogger().info("rewardChances:  " + rewardChances);}
				if(that.debug){that.getLogger().info("activeRewards:  " + activeRewards);}
				if(that.debug){that.getLogger().info("getKeys:  " + that.getConfig().getConfigurationSection("Rewards").getKeys(false));}
				double random;
				List<String> rewardsToGive = new ArrayList<String>();
				int element = -1;
				for (double chance : rewardChances) {
					element++;
					random = Math.random() * 100;
					if(that.debug){that.getLogger().info("Random number is " + random);}
					if (random < chance) {
						if(that.debug){that.getLogger().info(random + " < " + chance + ", so adding reward " + activeRewards.get(element) + " to rewardTrue.");}
						rewardsToGive.add(activeRewards.get(element));
					} else {
						if(that.debug){that.getLogger().info(random + " is NOT less than " + chance + ", so skipping reward " + activeRewards.get(element) + ".");}
					}
				}
				if (that.debug) {that.getLogger().info("rewardsToGive is now " + rewardsToGive);}
				List<String> commands;
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				for (String temp : rewardsToGive) {
					commands = (that.getConfig().getStringList("Rewards." + temp + ".commands"));
					for (String cmd : commands) {
						String placeheld = cmd.replace("%player%", player.getName());
						that.getLogger().info("Executing command:  " + placeheld);
						Bukkit.getServer().dispatchCommand(console, placeheld);
					}
				}
				if (that.debug) {that.getLogger().info("----------END OF REWARD CALCS----------");}
			}
		
	}
}