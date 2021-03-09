package me.Datatags.CommandMineRewards.gui.buttons.paginated;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.CommandMineRewards;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.gui.ItemBuilder;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;
import me.Datatags.CommandMineRewards.gui.guis.PaginatedGUI;

public abstract class PageButton extends GUIButton {
	private static final NamespacedKey pageTag = new NamespacedKey(CommandMineRewards.getInstance(), "page");
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.GUI;
	}

	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.GUI;
	}
	
	@Override
	protected ItemBuilder buildBase() {
		return new ItemBuilder(Material.ARROW).name(ChatColor.RESET + this.getItemName() + " Page");
	}

	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		return getBase().build();
	}
	
	public Integer getPageTag(ItemStack is) {
		return is.getItemMeta().getPersistentDataContainer().get(pageTag, PersistentDataType.INTEGER);
	}
	
	public void setPageTag(int page) {
		base.getItemMeta().getPersistentDataContainer().set(pageTag, PersistentDataType.INTEGER, page + getPageOffset());
	}
	
	public abstract int getPageOffset();
	protected abstract String getItemName();
	
	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		PaginatedGUI pageParent = (PaginatedGUI) parent;
		Integer page = getPageTag(is);
		pageParent.openFor(player, page);
	}
}
