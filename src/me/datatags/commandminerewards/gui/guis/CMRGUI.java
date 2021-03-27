package me.datatags.commandminerewards.gui.guis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.gui.CMRInventoryHolder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.buttons.general.BackButton;
import me.datatags.commandminerewards.gui.buttons.general.FillerButton;

public abstract class CMRGUI implements Cloneable {
	protected static Map<UUID,CMRGUI> users = new HashMap<>();
	protected GUIButton[][] gui = new GUIButton[6][9];
	
	public static void removeUser(Player player) {
		users.remove(player.getUniqueId());
	}
	
	public void openFor(Player player) {
		player.openInventory(generateInventory(player, gui));
		users.put(player.getUniqueId(), this);
	}
	protected Inventory generateInventory(Player player, GUIButton[][] toOpen) {
		Inventory inv = createInventory();
		if (getPreviousGUI() != null) {
			gui[5][4] = new BackButton();
		}
		for (int y = 0; y < toOpen.length; y++) {
			for (int x = 0; x < toOpen[0].length; x++) {
				GUIButton button = toOpen[y][x];
				if (skipFillers() && button == null) continue;
				if (button == null || button.getPermission() == null || !button.getPermission().test(player)) {
					button = new FillerButton();
				}
				inv.setItem(x + (y * 9), button.getIcon());
			}
		}
		return inv;
	}
	public abstract CMRGUI clone();
	protected Inventory createInventory() {
		Inventory inv = Bukkit.createInventory(new CMRInventoryHolder(this), gui.length * 9, this.getTitle());
		return inv;
	}
	public abstract String getTitle();  
	public boolean skipFillers() {
		return false;
	}
	public abstract CMRGUI getPreviousGUI();
	public void onClick(InventoryClickEvent e) {
		findClicked(e, gui);
	}
	public void findClicked(InventoryClickEvent e, GUIButton[][] active) {
		if (e.getClickedInventory() == null || e.getClickedInventory().equals(e.getView().getBottomInventory())) return;
		ItemStack item = e.getCurrentItem();
		if (item == null) return;
		String buttonClass = item.getItemMeta().getPersistentDataContainer().get(GUIButton.KEY, PersistentDataType.STRING);
		if (buttonClass == null) return;
		for (GUIButton[] row : active) {
			for (GUIButton button : row) {
				if (button == null) continue;
				if (button.getClass().getSimpleName().equals(buttonClass) && button.isButton(item)) {
					button.onClick((Player)e.getWhoClicked(), item, this, e.getClick());
					return; // I don't think we need to tell more than one button it was clicked ever
				}
			}
		}
	}
	public void addBackButton() {
		gui[5][4] = new BackButton();
	}
	public static CMRGUI delayOpenGUI(Player player, CMRGUI gui) {
		// you aren't supposed to open or close inventories while in the handler
		// of an inventory click event, so use this method instead.
		new BukkitRunnable() {
			@Override
			public void run() {
				gui.openFor(player);
			}
		}.runTaskLater(CommandMineRewards.getInstance(), 1);
		return gui; // convenience
	}
	public static void delayCloseGUI(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				player.closeInventory();
			}
		}.runTaskLater(CommandMineRewards.getInstance(), 1);
	}
	public void refreshAll() {
		for (Entry<UUID,CMRGUI> entry : users.entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			entry.setValue(entry.getValue().refreshSelf(player));
		}
	}
	public CMRGUI refreshSelf(Player player) {
		return delayOpenGUI(player, this.clone());
	}
}