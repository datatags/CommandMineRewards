package me.datatags.commandminerewards.gui.buttons.main;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.conversations.CMRConversationFactory;
import me.datatags.commandminerewards.gui.conversations.MultiplierPrompt;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class MultiplierButton extends GUIButton {
    private GlobalConfigManager gcm = GlobalConfigManager.getInstance();
    @Override
    public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {
        if (clickType.isRightClick()) {
            gcm.setMultiplier(1);
            holder.updateGUI();
            return;
        }
        CMRConversationFactory.startConversation(holder, new MultiplierPrompt());
    }

    @Override
    public CMRPermission getPermission() {
        return CMRPermission.MULTIPLIER;
    }

    @Override
    public CMRPermission getClickPermission() {
        return CMRPermission.MULTIPLIER_MODIFY;
    }

    @Override
    protected ItemBuilder build() {
        return new ItemBuilder(Material.GOLD_INGOT).name(ChatColor.YELLOW + "Multiplier")
                .lore(ChatColor.YELLOW + "Current multiplier: " + gcm.getMultiplier());
    }
    
    @Override
    public void addClickableLore(Player player) {
        base.lore(ChatColor.YELLOW + "Click to modify");
        if (gcm.getMultiplier() != 1) {
            base.lore(ChatColor.RED + "Right-click to set to 1");
        }
    }
}
