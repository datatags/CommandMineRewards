package me.datatags.commandminerewards.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
		if (e.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof CMRInventoryHolder) return;
		CMRGUI.removeUser((Player)e.getPlayer());
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		CMRGUI.removeUser(e.getPlayer());
	}
}
