package me.AlanZ.CommandMineRewards.worldguard;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import me.AlanZ.CommandMineRewards.CommandMineRewards;
import me.AlanZ.CommandMineRewards.GlobalConfigManager;
import me.AlanZ.CommandMineRewards.RewardSection;

public class WorldGuardManager {
	private static RegionChecker checker;
	private static boolean worldGuardLoaded = false;
	private static CommandMineRewards cmr;
	public static void init(CommandMineRewards cmr) {
		WorldGuardManager.cmr = cmr;
		checker = new RegionCheckerWG7x();
		if (worldGuardLoaded = checker.isWorldGuardLoaded()) {
			cmr.getLogger().info("Found WorldGuard.  Using to check regions.");
		} else {
			cmr.getLogger().info("Could not find WorldGuard, the allowedRegions settings will be ignored.");
		}
	}
	public static boolean isAllowedInRegions(RewardSection rewardSection, Block block) {
		List<String> allowedRegions;
		if (rewardSection.getAllowedRegions().size() > 0) {
			allowedRegions = rewardSection.getAllowedRegions();
		} else if (GlobalConfigManager.getGlobalAllowedRegions().size() > 0) {
			allowedRegions = GlobalConfigManager.getGlobalAllowedRegions();
		} else {
			return true;
		}
		return checker.isInRegion(allowedRegions, block);
	}
	public static boolean isValidRegion(String region) {
		if (!GlobalConfigManager.isValidatingWorldsAndRegions()) {
			return true;
		}
		for (String regionName : getAllRegions()) {
			if (regionName.equalsIgnoreCase(region)) {
				return true;
			}
		}
		return false;
	}
	public static List<String> getAllRegions() {
		return checker.getAllRegions();
	}
	public static boolean usingWorldGuard() { // public
		return worldGuardLoaded;
	}
	public static void registerRegionChecker(RegionChecker rc) {
		cmr.getLogger().info("Received registration for RegionChecker native version v" + rc.getNative());
		checker = rc;
	}
	public static int getWGMajorVersion() {
		return Integer.parseInt(Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion().split("\\.")[0]);
	}
}
