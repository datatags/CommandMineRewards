package me.Datatags.CommandMineRewards.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(e.getInventory().getHolder() instanceof CMRInventoryHolder)) return;
		CMRInventoryHolder holder = (CMRInventoryHolder) e.getInventory().getHolder();
		if (!holder.getGUI().skipFillers()) {
			e.setCancelled(true);
		}
		holder.getGUI().onClick(e);
	}
}
