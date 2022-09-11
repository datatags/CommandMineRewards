package me.datatags.commandminerewards.hook;

import org.bukkit.Location;
import org.bukkit.World;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import me.datatags.commandminerewards.hook.interfaces.WorldGuardHook;

public class WorldGuard7Hook implements WorldGuardHook {

    @Override
    public RegionManager getRegionManager(World world) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
    }

    @Override
    public ApplicableRegionSet getApplicableRegions(Location loc) {
        return getRegionManager(loc.getWorld()).getApplicableRegions(BukkitAdapter.asBlockVector(loc));
    }
    
}
