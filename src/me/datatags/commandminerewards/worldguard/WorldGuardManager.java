package me.datatags.commandminerewards.worldguard;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;

public class WorldGuardManager {
	private RegionChecker checker = null;
	private boolean useWorldGuard = false;
	public WorldGuardManager() {
		if (getWorldGuard() != null) {
			CMRLogger.info("Found WorldGuard.");
			RegionChecker rc = new RegionCheckerWG7x();
			if (checkWGVersion(rc)) {
				checker = rc;
				useWorldGuard = true;
			} else {
				CMRLogger.info("WorldGuard support will be disabled until otherwise noted.");
			}
		} else {
			CMRLogger.info("Could not find WorldGuard, the allowedRegions settings will be ignored.");
		}
	}
	public boolean isAllowedInRegions(RewardGroup rewardGroup, Block block) {
		GlobalConfigManager gcm = GlobalConfigManager.getInstance();
		List<String> allowedRegions;
		if (rewardGroup.getAllowedRegions().size() > 0) {
			allowedRegions = rewardGroup.getAllowedRegions();
		} else if (gcm.getGlobalAllowedRegions().size() > 0) {
			allowedRegions = gcm.getGlobalAllowedRegions();
		} else {
			return true;
		}
		if (allowedRegions.contains("*")) return true;
		return checker.isInRegion(allowedRegions, block);
	}
	public boolean isValidRegion(String region) {
		if (!GlobalConfigManager.getInstance().isValidatingWorldsAndRegions()) {
			return true;
		}
		for (String regionName : getAllRegions()) {
			if (regionName.equalsIgnoreCase(region)) {
				return true;
			}
		}
		return false;
	}
	public List<String> getAllRegions() {
		return checker.getAllRegions();
	}
	public boolean usingWorldGuard() { // public
		return useWorldGuard;
	}
	public void registerRegionChecker(RegionChecker rc) {
		if (checkWGVersion(rc)) {
			checker = rc;
			useWorldGuard = true;
		}
	}
	public int getWGMajorVersion() {
		return Integer.parseInt(getWorldGuard().getDescription().getVersion().split("\\.")[0]);
	}
	private Plugin getWorldGuard() {
		return Bukkit.getPluginManager().getPlugin("WorldGuard");
	}
	private boolean checkWGVersion(RegionChecker checker) {
		if (checker == null) {
			CMRLogger.warning("Something is wrong, please report this error: Something attempted to inject a null RegionChecker");
			return false;
		}
		int wgVersion = getWGMajorVersion();
		if (wgVersion != checker.getNative()) {
			CMRLogger.warning("A plugin has registered a RegionChecker for WorldGuard v" + checker.getNative() + " which is not installed.");
			return false;
		}
		CMRLogger.info("WorldGuard support for v" + wgVersion + " has been registered.");
		return true;
	}
}
