package me.datatags.commandminerewards.hook.interfaces;

import org.bukkit.Location;
import org.bukkit.World;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

public interface WorldGuardHook {
    public RegionManager getRegionManager(World world);
    public ApplicableRegionSet getApplicableRegions(Location loc);
}
