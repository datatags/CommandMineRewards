package me.datatags.commandminerewards.gui.guis;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.gui.CMRInventoryHolder;
import me.datatags.commandminerewards.gui.GUIManager;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.buttons.general.BackButton;
import me.datatags.commandminerewards.gui.buttons.general.FillerButton;

public abstract class CMRGUI implements Cloneable {
	protected static GUIManager gm = GUIManager.getInstance();
	protected GUIButton[][] gui = new GUIButton[6][9];
	public void openFor(Player player) {
		openFor(gm.getNewHolder(player, this));
	}
	
	public void openFor(GUIUserHolder holder) {
		holder.changeGUI(this);
		Player owner = holder.getOwner();
		Inventory inv = generateInventory(owner, gui);
		owner.openInventory(inv);
		for (UUID helper : holder.getHelpers()) {
			Player player = Bukkit.getPlayer(helper);
			CMRLogger.debug("Opening GUI for helper " + player.getName());
			player.openInventory(inv);
		}
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
	public CMRGUI clone() {
		return getNewSelf();
	}
	public abstract CMRGUI getNewSelf();
	protected Inventory createInventory() {
		Inventory inv = Bukkit.createInventory(new CMRInventoryHolder(this), gui.length * 9, this.getTitle());
		return inv;
	}
	public abstract String getTitle();  
	public boolean skipFillers() {
		return false;
	}
	public abstract CMRGUI getPreviousGUI();

	public abstract boolean isRewardInUse(String group, String reward); // if reward is null, deleting group, otherwise, deleting reward
	public void onClick(InventoryClickEvent e) {
		findClicked(e, gui, gm.getHolder((Player)e.getWhoClicked()));
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
						button.onClick(holder, item, this, e.getClick());
						gm.refreshAllExcept(holder);
					}
					return; // I don't think we need to tell more than one button it was clicked ever
				}
			}
		}
	}
	public void addBackButton() {
		gui[5][4] = new BackButton();
	}
	public GUIManager getGUIManager() {
		return gm;
	}
}
