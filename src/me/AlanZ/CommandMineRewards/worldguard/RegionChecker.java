package me.AlanZ.CommandMineRewards.worldguard;

import java.util.List;

import org.bukkit.block.Block;

public interface RegionChecker {
	public boolean isInRegion(List<String> regions, Block block);
	public List<String> getAllRegions();
	public boolean isWorldGuardLoaded();
	public int getNative();
}
