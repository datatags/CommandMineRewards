package me.datatags.commandminerewards.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class GUIListener implements Listener {
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(e.getInventory().getHolder() instanceof CMRInventoryHolder)) return;
		CMRInventoryHolder holder = (CMRInventoryHolder) e.getInventory().getHolder();
		e.setCancelled(true);
		holder.getGUI().onClick(e);
	}
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (!(e.getInventory().getHolder() instanceof CMRInventoryHolder)) return;
		CMRInventoryHolder invHolder = (CMRInventoryHolder) e.getInventory().getHolder();
		Player player = (Player)e.getPlayer(); // why doesn't e.getPlayer return a player directly :?
		GUIUserHolder holder = CMRGUI.getHolder(player);
		if (holder == null) return; // can happen if a player disconnects while the GUI is open
		if (holder.isConversing()) return; // we've moved to a conversation, ignore it
		if (holder.getGUI() == invHolder.getGUI()) { // should all be pointing to the same thing, so == should work
			// if the holder hasn't been updated by the time the event's been fired, it's been closed by the user
			CMRGUI.removeUser(player);
			CMRLogger.debug("Removing " + e.getPlayer().getName() + " from users");
		} else {
			CMRLogger.debug("Inventories for " + e.getPlayer().getName() + " did not match, must be update");
		}
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		CMRGUI.removeUser(e.getPlayer());
	}
}
