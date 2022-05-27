package me.datatags.commandminerewards.gui.buttons.general;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class FillerButton extends GUIButton {

    @Override
    public CMRPermission getPermission() {
        return CMRPermission.GUI;
    }

    @Override
    public CMRPermission getClickPermission() {
        return null;
    }

    @Override
    protected ItemBuilder build() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ");
    }

    @Override
    public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {}

}
