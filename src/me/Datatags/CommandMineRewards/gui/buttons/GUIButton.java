package me.Datatags.CommandMineRewards.gui.buttons;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.CommandMineRewards;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.gui.ItemBuilder;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;

public abstract class GUIButton {
	public static final NamespacedKey key = new NamespacedKey(CommandMineRewards.getInstance(), "buttonIdentifier");
	protected ItemBuilder base;
	public GUIButton() {
		
	}
	public abstract CMRPermission getPermission();
	public abstract CMRPermission getClickPermission();
	protected abstract ItemBuilder buildBase();
	protected ItemBuilder getBase() {
		if (base == null) {
			resetBase();
		}
		return base.clone();
	}
	public void resetBase() {
		base = buildBase();
		addIdentityTag();
	}
	public ItemStack getBaseItem() {
		return getBase().build();
	}
	protected abstract ItemStack personalize(Player player, GlobalConfigManager gcm);
	public ItemStack getButton(Player player) {
		return personalize(player, GlobalConfigManager.getInstance());
	}
	public boolean isButton(ItemStack is) { // override to distinguish from other buttons of the same type in the same GUI 
		if (is == null) return false;
		if (!is.hasItemMeta()) return false;
		if (!is.getItemMeta().hasDisplayName()) return false; // all buttons have item name, even if the name is " "
		return getBaseItem().getItemMeta().getDisplayName().equals(is.getItemMeta().getDisplayName());
	}
	public abstract void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType);
	protected void addIdentityTag() {
		base.getItemMeta().getPersistentDataContainer().set(key, PersistentDataType.STRING, this.getClass().getSimpleName());
	}
}
