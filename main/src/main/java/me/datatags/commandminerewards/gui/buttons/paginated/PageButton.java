package me.datatags.commandminerewards.gui.buttons.paginated;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.guis.CMRGUI;
import me.datatags.commandminerewards.gui.guis.PaginatedGUI;

public abstract class PageButton extends GUIButton {
    private static final NamespacedKey pageTag = new NamespacedKey(CommandMineRewards.getInstance(), "page");
    private int page = 0;
    @Override
    public CMRPermission getPermission() {
        return CMRPermission.GUI;
    }

    @Override
    public CMRPermission getClickPermission() {
        return CMRPermission.GUI;
    }
    
    @Override
    protected ItemBuilder build() {
        return new ItemBuilder(Material.ARROW).name(ChatColor.RESET + this.getItemName() + " Page");
    }
    
    public Integer getPageTag(ItemStack is) {
        return is.getItemMeta().getPersistentDataContainer().get(pageTag, PersistentDataType.INTEGER);
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    @Override
    public void addIdentityTag() {
        super.addIdentityTag();
        base.getItemMeta().getPersistentDataContainer().set(pageTag, PersistentDataType.INTEGER, page + getPageOffset());
    }
    
    public abstract int getPageOffset();
    protected abstract String getItemName();

    @Override
    public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {
        PaginatedGUI pageParent = (PaginatedGUI) parent;
        Integer page = getPageTag(is);
        new BukkitRunnable() {
            @Override
            public void run() {
                pageParent.clone().openFor(holder, page);
            }
        }.runTaskLater(CommandMineRewards.getInstance(), 1);
    }
    
}
