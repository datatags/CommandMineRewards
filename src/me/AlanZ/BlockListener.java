package me.AlanZ;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Bukkit;
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
		if (that.debug) {that.getLogger().info("Blocks list:  " + that.getConfig().getStringList("Blocks"));}
		Player player = e.getPlayer();
		if (player.hasPermission(that.usePermission)) {
			boolean blockFound = false; 
			for (String block : that.blocks) {
				if(e.getBlock().getType() == Material.valueOf(block.toUpperCase())) {
					blockFound = true;
					break;
				}
			}
			if (blockFound) {
				int rewardCount = (new HashSet<String>(that.getConfig().getConfigurationSection("Rewards").getKeys(false))).size();
				List<Double> rewardChances = new ArrayList<Double>();
				for (int i = 1; i < rewardCount + 1; i++) {				
					rewardChances.add((that.getConfig().getDouble("Rewards." + i + ".chance") * that.multiplier));
				}
				if(that.debug){that.getLogger().info("rewardChances:  " + rewardChances);}
				double random;
				List<Integer> rewardTrue = new ArrayList<Integer>();
				int element = -1;
				for (double chance : rewardChances) {
					element++;
					random = Math.floor(Math.random() * 100);
					if(that.debug){that.getLogger().info("Random number is " + random);}
					if (random < chance) {
						if(that.debug){that.getLogger().info(random + " < " + chance + ", so adding element " + element + " to rewardTrue.");}
						rewardTrue.add(element);
					} else {
						if(that.debug){that.getLogger().info(random + " is NOT less than " + chance + ", so skipping element " + element + ".");}
					}
				}
				if(that.debug){that.getLogger().info("rewardTrue is now " + rewardTrue);}
				List<String> commands;
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				for (int j : rewardTrue) {
					commands = (that.getConfig().getStringList("Rewards." + (j + 1) + ".commands"));
					for (String cmd : commands) {
						String placeheld = cmd.replace("%player%", player.getName());
						that.getLogger().info("Executing command:  " + placeheld);
						Bukkit.getServer().dispatchCommand(console, placeheld);
					}
				}
			}
		}
	}
}