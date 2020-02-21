package me.AlanZ.CommandMineRewards.worldguard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.AlanZ.CommandMineRewards.GlobalConfigManager;

public class RegionCheckerWG7x implements RegionChecker {
	@Override
	public boolean isInRegion(List<String> regions, Block block) {
		ApplicableRegionSet set = getRegionManager(block.getWorld()).getApplicableRegions(BukkitAdapter.asBlockVector(block.getLocation()));
		for (ProtectedRegion region : set) {
			if (GlobalConfigManager.containsIgnoreCase(regions, region.getId())) {
				return true;
			}
		}
		return false;
	}
	private static RegionManager getRegionManager(World world) {
		return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
	}
	@Override
	public List<String> getAllRegions() {
		List<String> ids = new ArrayList<String>();
		for (World world : Bukkit.getServer().getWorlds()) {
			for (ProtectedRegion pr : getRegionManager(world).getRegions().values()) {
				ids.add(pr.getId());
			}
		}
		return ids;
	}
	@Override
	public int getNative() {
		return 7;
	}
}
