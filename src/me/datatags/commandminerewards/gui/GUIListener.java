package me.datatags.commandminerewards.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class GUIListener implements Listener {
	private CommandMineRewards cmr;
	public GUIListener(CommandMineRewards cmr) {
		this.cmr = cmr;
	}
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
		Player player = (Player)e.getPlayer(); // why doesn't e.getPlayer return a player directly :?
		GUIUserHolder holder = CMRGUI.getHolder(player);
		if (Bukkit.getPlayer(holder.getOwner()).isConversing()) return; // TODO: check if they're actually conversing with CMR somehow?
		new BukkitRunnable() { // we can't check the post-close inventory in the event handler, so wait a tick and then check
			@Override
			public void run() {
				if (player.getOpenInventory().getTopInventory().getHolder() instanceof CMRInventoryHolder) return;
				CMRGUI.removeUser(player);
			}			
		}.runTaskLater(cmr, 1);
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		CMRGUI.removeUser(e.getPlayer());
	}
}
