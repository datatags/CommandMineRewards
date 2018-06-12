package me.AlanZ.CommandMineRewards;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionChecker {
	public static boolean isInRegion(List<String> regions, World world, Player player) {
		ApplicableRegionSet set = WGBukkit.getRegionManager(world).getApplicableRegions(player.getLocation());
		for (ProtectedRegion region : set) {
			if (GlobalConfigManager.containsIgnoreCase(regions, region.getId())) {
				return true;
			}
		}
		return false;
	}
}
