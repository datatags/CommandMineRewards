package me.datatags.commandminerewards.hook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;

public class WorldGuardManager {
	private boolean usingWorldGuard = false;
	private boolean legacy;
	// next properties are only for legacy
	private Object regionContainer;
	private Method getManagerMethod;
	private Method getApplicableRegionsMethod;
	public WorldGuardManager() {
		if (getWorldGuard() == null) {
			CMRLogger.info("Could not find WorldGuard, the allowedRegions settings will be ignored.");
			return;
		}
		CMRLogger.debug("Found WorldGuard.");
		try {
			Class.forName("com.sk89q.worldguard.WorldGuard");
			CMRLogger.debug("Identified WorldGuard version >= v7");
		} catch (ClassNotFoundException e) {
			try {
				Method regionContainerMethod = WorldGuardPlugin.class.getDeclaredMethod("getRegionContainer");
				CMRLogger.debug("Identified WorldGuard version <= v6");
				legacy = true;
				regionContainer = regionContainerMethod.invoke(WGBukkit.getPlugin());
				getManagerMethod = Class.forName("com.sk89q.worldguard.bukkit.RegionContainer").getDeclaredMethod("get", World.class);
				getApplicableRegionsMethod = RegionManager.class.getMethod("getApplicableRegions", Location.class);
			} catch (NoSuchMethodException ex) {
				CMRLogger.warning("I can't figure out what WorldGuard version you have, neither of the things I'm expecting are present. WorldGuard support will be disabled.");
				CMRLogger.debug(ex.getMessage());
				return;
			} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException ex) {
				CMRLogger.warning("Something unexpected happened when hooking WorldGuard:");
				ex.printStackTrace();
				CMRLogger.info("WorldGuard support will be disabled.");
				return;
			}
		}
		usingWorldGuard = true;
		CMRLogger.info("Successfully hooked WorldGuard!");
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
		return this.isInRegion(allowedRegions, block);
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
	public boolean isInRegion(List<String> regions, Block block) {
		RegionManager rm = getRegionManager(block.getWorld());
		ApplicableRegionSet set;
		if (legacy) {
			try {
				set = (ApplicableRegionSet) getApplicableRegionsMethod.invoke(rm, block.getLocation());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				CMRLogger.error("Something unexpected happened when trying to get regions of block");
				e.printStackTrace();
				return false;
			}
		} else {
			set = rm.getApplicableRegions(BukkitAdapter.asBlockVector(block.getLocation()));
		}
		for (ProtectedRegion region : set) {
			if (GlobalConfigManager.getInstance().containsIgnoreCase(regions, region.getId())) {
				return true;
			}
		}
		return false;
	}
	private RegionManager getRegionManager(World world) {
		if (legacy) {
			try {
				return (RegionManager) getManagerMethod.invoke(regionContainer, world);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				CMRLogger.error("Something unexpected happened when trying to get WorldGuard regions:");
				e.printStackTrace();
				return null;
			}
		} else {
			return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
		}
	}
	public List<String> getAllRegions() {
		List<String> ids = new ArrayList<String>();
		for (World world : Bukkit.getServer().getWorlds()) {
			for (ProtectedRegion pr : getRegionManager(world).getRegions().values()) {
				ids.add(pr.getId());
			}
		}
		return ids;
	}
	private Plugin getWorldGuard() {
		return Bukkit.getPluginManager().getPlugin("WorldGuard");
	}
	public boolean usingWorldGuard() { // public
		return usingWorldGuard;
	}
}
