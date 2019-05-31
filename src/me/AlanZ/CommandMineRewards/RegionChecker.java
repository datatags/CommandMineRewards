package me.AlanZ.CommandMineRewards;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionChecker {
	public static boolean isInRegion(List<String> regions, World world, Player player) {
		RegionManager rm = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
		ApplicableRegionSet set = rm.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()));
		for (ProtectedRegion region : set) {
			if (GlobalConfigManager.containsIgnoreCase(regions, region.getId())) {
				return true;
			}
		}
		return false;
	}
}
