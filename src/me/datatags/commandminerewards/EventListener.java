package me.datatags.commandminerewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class EventListener implements Listener {
	private CMRBlockManager cbm;
	private GlobalConfigManager gcm;
	private Map<Integer,BlockState> autopickupCompatData = new HashMap<>();
	public EventListener() {
		this.gcm = GlobalConfigManager.getInstance();
		this.cbm = CMRBlockManager.getInstance();
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreakEvent(BlockBreakEvent e) {
		BlockState state;
		if (gcm.isAutopickupCompat()) {
			if (!autopickupCompatData.containsKey(e.hashCode())) {
				CMRLogger.warning("No autopickup compat data found for block " + e.getBlock());
				return;
			}
			CMRLogger.debug("Loading block data from autopickup compat hash");
			state = autopickupCompatData.remove(e.hashCode());
		} else {
			state = e.getBlock().getState();
		}
		if (e.isCancelled()) {
			CMRLogger.debug("Player was denied access to all sections because event was cancelled.");
			return;
		}
		if (state.getType() == Material.AIR) {
			CMRLogger.debug("Player was denied access to all sections because CMR received air block in event handler??");
			return;
		}
		cbm.executeAllSections(state, e.getPlayer());
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void autopickupCompatListener(BlockBreakEvent e) {
		if (gcm.isAutopickupCompat()) {
			autopickupCompatData.put(e.hashCode(), e.getBlock().getState());
		}
	}
}