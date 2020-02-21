package me.AlanZ.CommandMineRewards.worldguard;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import me.AlanZ.CommandMineRewards.CommandMineRewards;
import me.AlanZ.CommandMineRewards.GlobalConfigManager;
import me.AlanZ.CommandMineRewards.RewardSection;

public class WorldGuardManager {
	private static RegionChecker checker;
	private static boolean useWorldGuard = false;
	private static CommandMineRewards cmr;
	public static void init(CommandMineRewards cmr) {
		WorldGuardManager.cmr = cmr;
		if (useWorldGuard = (getWorldGuard() != null)) {
			cmr.getLogger().info("Found WorldGuard.");
			RegionChecker rc = new RegionCheckerWG7x();
			if (checkWGVersion(rc)) checker = rc;
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
		return useWorldGuard;
	}
	public static void registerRegionChecker(RegionChecker rc) {
		if (checkWGVersion(rc)) {
			checker = rc;
		}
	}
	public static int getWGMajorVersion() {
		return Integer.parseInt(getWorldGuard().getDescription().getVersion().split("\\.")[0]);
	}
	private static Plugin getWorldGuard() {
		return Bukkit.getPluginManager().getPlugin("WorldGuard");
	}
	private static boolean checkWGVersion(RegionChecker checker) {
		if (checker == null) {
			cmr.getLogger().info("Something is wrong, please report this error: Something attempted to inject a null RegionChecker");
			return false;
		}
		int wgVersion = getWGMajorVersion();
		if (wgVersion != checker.getNative()) {
			cmr.getLogger().info("A plugin has registered a RegionChecker for WorldGuard v" + checker.getNative() + " which is not installed.");
			return false;
		}
		cmr.getLogger().info("WorldGuard support for v" + wgVersion + " has been registered.");
		return true;
	}
}
