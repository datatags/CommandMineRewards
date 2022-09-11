package me.datatags.commandminerewards.hook.legacy;

import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import me.datatags.commandminerewards.hook.interfaces.WorldGuardHook;

public class WorldGuard6Hook implements WorldGuardHook {

    @Override
    public RegionManager getRegionManager(World world) {
        return WGBukkit.getRegionManager(world);
    }

    @Override
    public ApplicableRegionSet getApplicableRegions(Location loc) {
        return getRegionManager(loc.getWorld()).getApplicableRegions(loc);
    }

}
