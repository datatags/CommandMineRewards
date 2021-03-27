package me.datatags.commandminerewards.gui.buttons;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public abstract class GUIButton {
	public static final NamespacedKey KEY = new NamespacedKey(CommandMineRewards.getInstance(), "buttonIdentifier");
	protected ItemBuilder base;
	public GUIButton() {
		
	}
	public abstract CMRPermission getPermission();
	public abstract CMRPermission getClickPermission();
	protected abstract ItemBuilder build();
	protected ItemBuilder getBase() {
		if (base == null) {
			resetBase();
		}
		return base.clone();
	}
	public void resetBase() {
		base = build();
		addIdentityTag();
	}
	public ItemStack getIcon() {
		return getBase().build();
	}
	public boolean isButton(ItemStack is) { // override to distinguish from other buttons of the same type in the same GUI 
		if (is == null) return false;
		if (!is.hasItemMeta()) return false;
		if (!is.getItemMeta().hasDisplayName()) return false; // all buttons have item name, even if the name is " "
		return getIcon().getItemMeta().getDisplayName().equals(is.getItemMeta().getDisplayName());
	}
	public abstract void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType);
	protected void addIdentityTag() {
		base.getItemMeta().getPersistentDataContainer().set(KEY, PersistentDataType.STRING, this.getClass().getSimpleName());
	}
}
