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
	
	//NOTE:  Any lines that are commented out are for debugging.
	
	@EventHandler
	public void onEvent(BlockBreakEvent e) {
		Player player = e.getPlayer();
		if (player.hasPermission(that.usePermission)) {
			boolean blockFound = false; 
//			List<String> blocks = (ArrayList<String>) that.getConfig().getStringList("Blocks");
			for (String block : that.blocks) {
				if(e.getBlock().getType() == Material.valueOf(block.toUpperCase())) {
					blockFound = true;
					break;
				}
			}
			if (blockFound) {
				int rewardCount = (new HashSet<String>(that.getConfig().getConfigurationSection("Rewards").getKeys(false))).size();
				List<Integer> rewardChances = new ArrayList<Integer>();
				for (int i = 1; i < rewardCount + 1; i++) {				
					rewardChances.add((that.getConfig().getInt("Rewards." + i + ".chance") * that.multiplier));
				}
//				that.getLogger().info("rewardChances:  " + rewardChances);
				int random;
				List<Integer> rewardTrue = new ArrayList<Integer>();
				int element = -1;
				for (int chance : rewardChances) {
					element++;
					random = (int) Math.floor(Math.random() * 100);
					that.getLogger().info("Random number is " + random);
					if (random < chance) {
//						that.getLogger().info(random + " < " + chance + ", so adding element " + element + " to rewardTrue.");
						rewardTrue.add(element);
					}// else {
//						that.getLogger().info(random + " is NOT less than " + chance + ", so skipping element " + element + ".");
//					}
				}
//				that.getLogger().info("rewardTrue is now " + rewardTrue);
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