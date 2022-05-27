package me.datatags.commandminerewards.gui.buttons;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public abstract class GUIButton {
    public static final NamespacedKey KEY = new NamespacedKey(CommandMineRewards.getInstance(), "buttonIdentifier");
    protected ItemBuilder base;
    
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
    public ItemStack getIcon(Player player) {
        resetBase(); // do we need to clear every time or is there a better way ?????
        if (this.getClickPermission() != null && this.getClickPermission().test(player)) {
            addClickableLore(player);
        }
        return base.build();
    }
    public void addClickableLore(Player player) {} // mostly used for permission checks
    public boolean isButton(ItemStack is) { // override to distinguish from other buttons of the same type in the same GUI 
        if (is == null) return false;
        if (is.getType() != getBase().getType()) return false;
        if (!getBase().hasName()) return true;
        
        if (!is.hasItemMeta()) return false;
        if (!is.getItemMeta().hasDisplayName()) return false;
        return getBase().getItemMeta().getDisplayName().equals(is.getItemMeta().getDisplayName());
    }
    public abstract void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType);
    protected void addIdentityTag() {
        base.getItemMeta().getPersistentDataContainer().set(KEY, PersistentDataType.STRING, this.getClass().getSimpleName());
    }
}
