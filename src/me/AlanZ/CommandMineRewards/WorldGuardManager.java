package me.AlanZ.CommandMineRewards;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardManager {
	public static boolean isAllowedInRegions(RewardSection rewardSection, Player player) {
		List<String> allowedRegions;
		if (rewardSection.getAllowedWorlds().size() > 0) {
			allowedRegions = rewardSection.getAllowedRegions();
		} else if (GlobalConfigManager.getGlobalAllowedRegions().size() > 0) {
			allowedRegions = GlobalConfigManager.getGlobalAllowedRegions();
		} else {
			return true;
		}
		ApplicableRegionSet set = getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
		for (ProtectedRegion rg : set) {
			if (GlobalConfigManager.containsIgnoreCase(allowedRegions, rg.getId())) {
				return true;
			}
		}
		return false;
	}
	public static boolean isValidRegion(String region) {
		if (!GlobalConfigManager.isValidatingWorldsAndRegions()) {
			return true;
		}
		for (World world : Bukkit.getServer().getWorlds()) {
			for (ProtectedRegion pr : getRegionManager(world).getRegions().values()) {
				if (pr.getId().equalsIgnoreCase(region)) {
					return true;
				}
			}
		}
		return false;
	}
	private static RegionManager getRegionManager(World world) {
		WorldGuardPlugin worldGuard = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		return worldGuard.getRegionManager(world);
	}
}
