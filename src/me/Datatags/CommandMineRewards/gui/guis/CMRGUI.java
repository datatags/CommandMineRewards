package me.Datatags.CommandMineRewards.gui.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import me.Datatags.CommandMineRewards.CommandMineRewards;
import me.Datatags.CommandMineRewards.gui.CMRInventoryHolder;
import me.Datatags.CommandMineRewards.gui.GUIManager;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.BackButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.FillerButton;

public abstract class CMRGUI {
	private int size = 54;
	protected GUIButton[][] gui = new GUIButton[6][9];
	private GUIManager gm;
	public CMRGUI() {
		this.gm = GUIManager.getInstance();
	}
	public void openFor(Player player) {
		player.openInventory(generateInventory(player, gui));
	}
	protected Inventory generateInventory(Player player, GUIButton[][] toOpen) {
		Inventory inv = createInventory();
		if (getPreviousGUI() != null) {
			gui[5][4] = new BackButton();
		}
		for (int y = 0; y < toOpen.length; y++) {
			for (int x = 0; x < toOpen[0].length; x++) {
				GUIButton button = toOpen[y][x];
				if (!skipFillers() && (button == null || button.getPermission() == null || !button.getPermission().test(player))) {
					button = new FillerButton();
				}
				inv.setItem(x + (y * 9), button.getButton(player));
			}
		}
		return inv;
	}
	protected Inventory createInventory() {
		return Bukkit.createInventory(new CMRInventoryHolder(this), size);
	}
	public abstract String getTitle();  
	public boolean skipFillers() {
		return false;
	}
	public abstract CMRGUI getPreviousGUI();
	public GUIManager getGUIManager() {
		return gm;
	}
	public void onClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if (item == null) return;
		String buttonClass = item.getItemMeta().getPersistentDataContainer().get(GUIButton.key, PersistentDataType.STRING);
		if (buttonClass == null) return;
		for (GUIButton[] row : gui) {
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
	public void delayOpenGUI(Player player, CMRGUI gui) {
		// you aren't supposed to open or close inventories while in the handler
		// of an inventory click event, so use this method instead.
		new BukkitRunnable() {
			@Override
			public void run() {
				gui.openFor(player);
			}
		}.runTaskLater(CommandMineRewards.getInstance(), 1);
	}
	public void refreshGUI(Player player) {
		delayOpenGUI(player, this);
	}
}
