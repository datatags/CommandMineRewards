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

import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.gui.CMRInventoryHolder;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.buttons.general.BackButton;
import me.datatags.commandminerewards.gui.buttons.general.FillerButton;

public abstract class CMRGUI implements Cloneable {
	protected static Map<UUID,GUIUserHolder> users = new HashMap<>();
	protected GUIButton[][] gui = new GUIButton[6][9];
	
	public static void removeUser(Player player) {
		GUIUserHolder ownerHolder = users.remove(player.getUniqueId());
		if (ownerHolder != null) {
			ownerHolder.clear();
			return;
		}
		for (GUIUserHolder holder : users.values()) {
			if (holder.containsUser(player)) {
				holder.removeHelper(player);
				return;
			}
		}
	}
	
	public void openFor(Player player) {
		openFor(getNewHolder(player));
	}
	
	public void openFor(GUIUserHolder holder) {
		holder.changeGUI(this);
		Player owner = Bukkit.getPlayer(holder.getOwner());
		Inventory inv = generateInventory(owner, gui);
		owner.openInventory(inv);
		for (UUID helper : holder.getHelpers()) {
			Player player = Bukkit.getPlayer(helper);
			CMRLogger.debug("Opening GUI for helper " + player.getName());
			player.openInventory(inv);
		}
	}
	
	protected GUIUserHolder getNewHolder(Player player) {
		GUIUserHolder playerHolder = getHolder(player);
		boolean owner = playerHolder != null && playerHolder.getOwner().equals(player.getUniqueId());
		if (playerHolder != null && !owner) {
			playerHolder.removeHelper(player);
			playerHolder = null;
		}
		if (playerHolder == null) {
			playerHolder = new GUIUserHolder(player, this);
			users.put(player.getUniqueId(), playerHolder);
		}
		return playerHolder;
	}
	
	public static GUIUserHolder getHolder(Player player) {
		for (GUIUserHolder holder : users.values()) {
			if (holder.containsUser(player)) {
				return holder;
			}
		}
		return null;
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
				inv.setItem(x + (y * 9), button.getIcon(player));
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
		findClicked(e, gui, getHolder((Player)e.getWhoClicked()));
	}
	public void findClicked(InventoryClickEvent e, GUIButton[][] active, GUIUserHolder holder) {
		if (e.getClickedInventory() == null || e.getClickedInventory().equals(e.getView().getBottomInventory())) return;
		ItemStack item = e.getCurrentItem();
		if (item == null) return;
		String buttonClass = item.getItemMeta().getPersistentDataContainer().get(GUIButton.KEY, PersistentDataType.STRING);
		if (buttonClass == null) return;
		for (GUIButton[] row : active) {
			for (GUIButton button : row) {
				if (button == null) continue;
				if (button.getClass().getSimpleName().equals(buttonClass) && button.isButton(item)) {
					if (button.getClickPermission() != null && button.getClickPermission().test(e.getWhoClicked())) {
						button.onClick(Bukkit.getPlayer(holder.getOwner()), item, this, e.getClick());
					}
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
		for (Entry<UUID,GUIUserHolder> entry : users.entrySet()) {
			entry.getValue().updateGUI();
		}
	}
	public CMRGUI refreshSelf(Player player) {
		return delayOpenGUI(player, this.clone());
	}
}
