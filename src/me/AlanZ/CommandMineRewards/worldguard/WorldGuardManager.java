package me.AlanZ.CommandMineRewards.worldguard;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import me.AlanZ.CommandMineRewards.CommandMineRewards;
import me.AlanZ.CommandMineRewards.GlobalConfigManager;
import me.AlanZ.CommandMineRewards.RewardSection;

public class WorldGuardManager {
	private RegionChecker checker = null;
	private boolean useWorldGuard = false;
	private CommandMineRewards cmr;
	public WorldGuardManager(CommandMineRewards cmr) {
		this.cmr = cmr;
		if (getWorldGuard() != null) {
			cmr.info("Found WorldGuard.");
			RegionChecker rc = new RegionCheckerWG7x();
			if (checkWGVersion(rc)) {
				checker = rc;
				useWorldGuard = true;
			} else {
				cmr.info("WorldGuard support will be disabled until otherwise noted.");
			}
		} else {
			cmr.info("Could not find WorldGuard, the allowedRegions settings will be ignored.");
		}
	}
	public boolean isAllowedInRegions(RewardSection rewardSection, Block block) {
		GlobalConfigManager gcm = GlobalConfigManager.getInstance();
		List<String> allowedRegions;
		if (rewardSection.getAllowedRegions().size() > 0) {
			allowedRegions = rewardSection.getAllowedRegions();
		} else if (gcm.getGlobalAllowedRegions().size() > 0) {
			allowedRegions = gcm.getGlobalAllowedRegions();
		} else {
			return true;
		}
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
			cmr.warning("Something is wrong, please report this error: Something attempted to inject a null RegionChecker");
			return false;
		}
		int wgVersion = getWGMajorVersion();
		if (wgVersion != checker.getNative()) {
			cmr.warning("A plugin has registered a RegionChecker for WorldGuard v" + checker.getNative() + " which is not installed.");
			return false;
		}
		cmr.info("WorldGuard support for v" + wgVersion + " has been registered.");
		return true;
	}
}
