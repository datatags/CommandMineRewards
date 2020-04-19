package me.Datatags.CommandMineRewards;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class EventListener implements Listener {
	private CMRBlockManager cbm;
	public EventListener(CommandMineRewards plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.cbm = CMRBlockManager.getInstance();
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreakEvent(BlockBreakEvent e) {
		cbm.executeAllSections(e.getBlock(), e.getPlayer());
	}
}