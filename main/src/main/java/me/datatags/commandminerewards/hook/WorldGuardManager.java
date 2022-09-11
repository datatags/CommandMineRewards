package me.datatags.commandminerewards.hook;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.hook.interfaces.WorldGuardHook;
import me.datatags.commandminerewards.hook.legacy.WorldGuard6Hook;

public class WorldGuardManager {
    private boolean usingWorldGuard = false;
    private WorldGuardHook hook;
    public WorldGuardManager() {
        if (getWorldGuard() == null) {
            CMRLogger.info("Could not find WorldGuard, the allowedRegions settings will be ignored.");
            return;
        }
        CMRLogger.debug("Found WorldGuard.");
        try {
            Class.forName("com.sk89q.worldguard.WorldGuard");
            CMRLogger.debug("Identified WorldGuard version >= v7");
            hook = new WorldGuard7Hook();
        } catch (ClassNotFoundException e) {
            CMRLogger.debug("Identified WorldGuard version <= v6");
            hook = new WorldGuard6Hook();
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
        ApplicableRegionSet set = hook.getApplicableRegions(block.getLocation());
        for (ProtectedRegion region : set) {
            if (GlobalConfigManager.getInstance().containsIgnoreCase(regions, region.getId())) {
                return true;
            }
        }
        return false;
    }

    public List<String> getAllRegions() {
        List<String> ids = new ArrayList<>();
        for (World world : Bukkit.getServer().getWorlds()) {
            for (ProtectedRegion pr : hook.getRegionManager(world).getRegions().values()) {
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
