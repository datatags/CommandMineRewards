package me.AlanZ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class EventListener implements Listener {
	
	CommandMineRewards that;
	public EventListener(CommandMineRewards plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);	
		this.that = plugin;
	}
	private Set<String> getNames(Set<ConfigurationSection> sections) {
		Set<String> names = new HashSet<String>();
		for (ConfigurationSection sec : sections) {
			names.add(sec.getName());
		}
		return names;
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		List<ConfigurationSection> handlers = that.blockHandled(e.getBlock().getType(), e.getBlock().getData());
		if (handlers.size() > 0) {
			if (that.debug) {that.getLogger().info("---------START OF REWARD CALCS---------");}
			Player player = e.getPlayer();
			GameMode gm = player.getGameMode();
			for (ConfigurationSection rewardSection : handlers) {
				// No longer needed   int rewardCount = (new HashSet<String>(that.getConfig().getConfigurationSection("Rewards").getKeys(false))).size();
				/*List<Double> rewardChances = new ArrayList<Double>();
				List<String> activeRewards = new ArrayList<String>();*/
				Map<ConfigurationSection,Double> activeRewards = new HashMap<ConfigurationSection, Double>();
				for (ConfigurationSection reward : that.getConfigSections(rewardSection.getName() + ".rewards")) {
					if (that.isWorldAllowed(rewardSection, player.getWorld().getName())) {
						if (!reward.isDouble("chance") && !reward.isInt("chance")) {
							that.debug("Could not find chance property in reward " + reward.getName() + " in section " + rewardSection.getName());
							continue;
						}
						if (player.hasPermission("cmr.use." + rewardSection.getName() + "." + reward.getName()) || player.hasPermission(that.allRewardsPermission)) { //This big mess means just checks to see if the player has appropriate permissions of any sort.
							if ((that.survivalOnly && gm == GameMode.SURVIVAL) || !that.survivalOnly) {
								if (e.getPlayer().getInventory().getItemInMainHand() == null) {
									if (that.isSilkTouchAllowed(rewardSection, reward, false)) {
										activeRewards.put(reward, reward.getDouble("chance") * that.multiplier);
									} else {
										that.debug("Player was denied access to reward because of the presence or absence of silk touch");
									}
								} else {
									if (that.isSilkTouchAllowed(rewardSection, reward, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0)) {
										activeRewards.put(reward, reward.getDouble("chance") * that.multiplier);
									} else {
										that.debug("Player was denied access to reward because of the presence or absence of silk touch");
									}
								}
							} else if (that.debug){
								that.getLogger().info("Player " + player.getName() + " did not receive reward " + reward.getName() + " because they were in the wrong game mode!");
							}
						} else if (that.debug) {
							that.getLogger().info("Player " + player.getName() + " did not receive reward " + reward.getName() + " for lack of permission!");
						}
					} else {
						that.debug("Player was denied access to reward because that reward is not allowed in this world.");
					}
				}
				if(that.debug){that.getLogger().info("rewardChances:  " + activeRewards.values());}
				if(that.debug){that.getLogger().info("activeRewards:  " + getNames(activeRewards.keySet()));}
				double random;
				List<ConfigurationSection> rewardsToGive = new ArrayList<ConfigurationSection>();
				//int element = -1;
				for (ConfigurationSection reward : activeRewards.keySet()) {
					//element++;
					random = Math.random() * 100;
					double chance = activeRewards.get(reward);
					if(that.debug){that.getLogger().info("Random number is " + random);}
					if (random < chance) {
						if(that.debug){that.getLogger().info(random + " < " + chance + ", so adding reward " + reward.getName() + " to rewardTrue.");}
						rewardsToGive.add(reward);
					} else if (that.debug) {
						that.getLogger().info(random + " is NOT less than " + chance + ", so skipping reward " + reward.getName() + ".");
					}
				}
				//if (that.debug) {that.getLogger().info("rewardsToGive is now " + rewardsToGive);}
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				for (ConfigurationSection reward : rewardsToGive) {
					List<String> commands = (reward.getStringList("commands"));
					for (String cmd : commands) {
						String placeheld = cmd.replace("%player%", player.getName());
						that.getLogger().info("Executing command:  " + placeheld);
						Bukkit.getServer().dispatchCommand(console, placeheld);
					}
				}
			}
			if (that.debug) {that.getLogger().info("----------END OF REWARD CALCS----------");}
		}
	}
}