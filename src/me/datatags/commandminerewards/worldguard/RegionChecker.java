package me.datatags.commandminerewards.worldguard;

import java.util.List;

import org.bukkit.block.Block;

public interface RegionChecker {
	public boolean isInRegion(List<String> regions, Block block);
	public List<String> getAllRegions();
	public int getNative();
}
