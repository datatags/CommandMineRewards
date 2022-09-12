package me.datatags.commandminerewards;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.datatags.commandminerewards.hook.McMMOManager;

public class EventListener implements Listener {
    private final GlobalConfigManager gcm;
    private final CMRBlockManager cbm;
    private final McMMOManager mcmmo;
    private Map<Integer,BlockState> autopickupCompatData = new HashMap<>();
    private Set<Integer> mcMMOPlayerBlockData = new HashSet<>();

    public EventListener(McMMOManager mcmmo) {
        this.gcm = GlobalConfigManager.getInstance();
        this.cbm = CMRBlockManager.getInstance();
        this.mcmmo = mcmmo;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreakEvent(BlockBreakEvent e) {
        boolean mcMMOCheckPassed = true;
        if (mcMMOPlayerBlockData.remove(e.hashCode())) {
            CMRLogger.debug("Player was denied access to all sections because the block was player-placed according to mcMMO.");
            CMRLogger.debug("If this is not desired, set mcMMOHookEnabled to false in config.yml");
            mcMMOCheckPassed = false;
        }
        BlockState state;
        if (gcm.isAutopickupCompat()) {
            if (!autopickupCompatData.containsKey(e.hashCode())) {
                CMRLogger.warning("No autopickup compat data found for block " + e.getBlock());
                return;
            }
            CMRLogger.debug("Loading block data from autopickup compat map");
            state = autopickupCompatData.remove(e.hashCode());
        } else {
            state = e.getBlock().getState();
        }
        // delay return so we also remove any applicable autopickup data
        if (!mcMMOCheckPassed) return;
        if (e.isCancelled() && !gcm.isDisableCancelledCheck()) {
            CMRLogger.debug("Player was denied access to all sections because event was cancelled.");
            CMRLogger.debug("This is often caused by block claim plugins, and is normal behavior of the plugin.");
            CMRLogger.debug("If this is outside a claim, you may have a misbehaving auto-pickup plugin, see CMR FAQ for more info.");
            return;
        }
        if (state.getType() == Material.AIR) {
            CMRLogger.debug("CMR was told someone broke an air block?!");
            if (!gcm.isAutopickupCompat()) {
                CMRLogger.debug("This can be caused by auto-pickup plugins, if this is the case, enable autopickupCompat in config.yml");
            }
            return;
        }
        cbm.executeAllGroups(state, e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void autopickupCompatListener(BlockBreakEvent e) {
        // we have to do this because mcMMO also uses listener priority MONITOR to update the block store
        // and sometimes mcMMO goes first, which causes CMR to always register the block as non-player-placed
        if (mcmmo.isTrue(e.getBlock().getState())) {
            mcMMOPlayerBlockData.add(e.hashCode());
        }
        if (gcm.isAutopickupCompat()) {
            autopickupCompatData.put(e.hashCode(), e.getBlock().getState());
        }
    }
}