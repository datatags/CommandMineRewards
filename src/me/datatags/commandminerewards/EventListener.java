package me.datatags.commandminerewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.datatags.commandminerewards.hook.McMMOHook;

public class EventListener implements Listener {
	private CMRBlockManager cbm;
	private GlobalConfigManager gcm;
	private McMMOHook mh;
	private Map<Integer,BlockState> autopickupCompatData = new HashMap<>();
	private boolean airBlockWarned = false;
	public EventListener(McMMOHook mh) {
		this.gcm = GlobalConfigManager.getInstance();
		this.cbm = CMRBlockManager.getInstance();
		this.mh = mh;
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
			CMRLogger.debug("This is often caused by block claim plugins, and is normal behavior of the plugin.");
			CMRLogger.debug("If this is outside a claim, you may have a misbehaving auto-pickup plugin, see CMR FAQ for more info.");
			return;
		}
		if (state.getType() == Material.AIR) {
			if (airBlockWarned) return;
			CMRLogger.warning("CMR was told someone broke an air block?!");
			CMRLogger.info("This can be caused by auto-pickup plugins, if this is the case, enable autopickupCompat in config.yml");
			CMRLogger.info("This message will not show up again until the next server restart.");
			airBlockWarned = true;
			return;
		}
		if (mh != null && mh.getPlaceStore().isTrue(state)) {
			CMRLogger.debug("Player was denied access to all sections because the block was player-placed according to mcMMO.");
			CMRLogger.debug("If this is not desired, set mcMMOHookEnabled to false in config.yml");
			return;
		}
		cbm.executeAllGroups(state, e.getPlayer());
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void autopickupCompatListener(BlockBreakEvent e) {
		if (gcm.isAutopickupCompat()) {
			autopickupCompatData.put(e.hashCode(), e.getBlock().getState());
		}
	}
}