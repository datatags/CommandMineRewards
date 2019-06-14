package me.AlanZ.CommandMineRewards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class EventListener implements Listener {
	CommandMineRewards cmr;
	public EventListener(CommandMineRewards plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);	
		this.cmr = plugin;
	}
	private Set<String> getNames(Set<Reward> rewards) {
		Set<String> names = new HashSet<String>();
		for (Reward reward : rewards) {
			names.add(reward.getName());
		}
		return names;
	}
	private void debug(String message) {
		cmr.debug(message);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreakEvent(BlockBreakEvent e) {
		List<RewardSection> handlers = GlobalConfigManager.getBlockHandlers(e.getBlock());
		if (handlers.size() > 0) {
			debug("---------START OF REWARD CALCS---------");
			if (e.isCancelled()) {
				debug("Player was denied access to reward because the block break was cancelled by another plugin.");
				return;
			}
			Player player = e.getPlayer();
			GameMode gm = player.getGameMode();
			for (RewardSection rewardSection : handlers) {
				// No longer needed   int rewardCount = (new HashSet<String>(cmr.getConfig().getConfigurationSection("Rewards").getKeys(false))).size();
				/*List<Double> rewardChances = new ArrayList<Double>();
				List<String> activeRewards = new ArrayList<String>();*/
				debug("processing reward section " + rewardSection.getName());
				Map<Reward,Double> activeRewards = new HashMap<Reward, Double>();
				if (!GlobalConfigManager.isWorldAllowed(rewardSection, player.getWorld().getName())) {
					debug("Player was denied access to rewards in reward section because the reward section is not allowed in this world.");
					continue;
				}
				if (cmr.usingWorldGuard() && !WorldGuardManager.isAllowedInRegions(rewardSection, player)) {
					debug("Player was denied access to rewards in reward section because the reward section is not allowed in this region.");
					continue;
				}
				for (Reward reward : rewardSection.getChildren()) {
					if (reward.getChance() == 0) {
						cmr.debug("Warning! Chance property was 0, invalid, or nonexistant in reward " + reward.getName() + " in section " + rewardSection.getName());
						continue;
					}
					if (!(player.hasPermission("cmr.use." + rewardSection.getName() + "." + reward.getName()) || player.hasPermission(cmr.allRewardsPermission))) { //This big mess means just checks to see if the player has appropriate permissions of any sort.
						debug("Player " + player.getName() + " did not receive reward " + reward.getName() + " for lack of permission!");
						continue;
					}
					if (!((GlobalConfigManager.getSurvivalOnly() && gm == GameMode.SURVIVAL) || !GlobalConfigManager.getSurvivalOnly())) {
						debug("Player " + player.getName() + " did not receive reward " + reward.getName() + " because they were in the wrong game mode!");
						continue;
					}
					if (cmr.getItemInHand(e.getPlayer()) == null) {
						if (GlobalConfigManager.silkStatusAllowed(rewardSection, reward, false)) {
							activeRewards.put(reward, reward.getChance() * GlobalConfigManager.getMultiplier());
						} else {
							cmr.debug("Player was denied access to reward because of the presence or absence of silk touch");
						}
					} else {
						if (GlobalConfigManager.silkStatusAllowed(rewardSection, reward, cmr.getItemInHand(e.getPlayer()).getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0)) {
							activeRewards.put(reward, reward.getChance() * GlobalConfigManager.getMultiplier());
						} else {
							cmr.debug("Player was denied access to reward because of the presence or absence of silk touch");
						}
					}
				}
				debug("rewardChances:  " + activeRewards.values());
				debug("activeRewards:  " + getNames(activeRewards.keySet()));
				double random;
				List<Reward> rewardsToGive = new ArrayList<Reward>();
				//int element = -1;
				for (Reward reward : activeRewards.keySet()) {
					//element++;
					random = Math.random() * 100;
					double chance = activeRewards.get(reward);
					debug("Random number is " + random);
					if (random < chance) {
						debug(random + " < " + chance + ", so adding reward " + reward.getName() + " to rewardTrue.");
						rewardsToGive.add(reward);
					} else {
						debug(random + " is NOT less than " + chance + ", so skipping reward " + reward.getName() + ".");
					}
				}
				//debug("rewardsToGive is now " + rewardsToGive);
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				for (Reward reward : rewardsToGive) {
					List<String> commands = (reward.getCommands());
					for (String cmd : commands) {
						String placeheld = cmd.replace("%player%", player.getName());
						if (GlobalConfigManager.getVerbosity() > 0) { // 0 = all logging disabled so don't log it if that's the case.
							cmr.getLogger().info("Executing command:  " + placeheld);
						}
						Bukkit.getServer().dispatchCommand(console, placeheld);
					}
				}
			}
			debug("----------END OF REWARD CALCS----------");
		}
	}
}