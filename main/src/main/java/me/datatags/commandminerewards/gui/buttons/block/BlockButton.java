package me.datatags.commandminerewards.gui.buttons.block;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRBlockState;
import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.BlockNotInListException;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class BlockButton extends GUIButton {
    private final RewardGroup group;
    private final CMRBlockState state;

    public BlockButton(RewardGroup group, CMRBlockState state) {
        this.group = group;
        this.state = state;
    }

    @Override
    public CMRPermission getPermission() {
        return CMRPermission.GUI;
    }

    @Override
    public CMRPermission getClickPermission() {
        return CMRPermission.BLOCK_MODIFY;
    }

    @Override
    protected ItemBuilder build() {
        ItemBuilder ib;
        if (state.getType().isItem()) {
            ib = new ItemBuilder(state.getType());
        } else {
            ib = new ItemBuilder(Material.PAPER).name(state.getType().toString());
        }

        if (state.getGrowth() != null) {
            ib.lore(ChatColor.YELLOW + "Data: " + (state.getGrowth() ? ChatColor.GREEN : ChatColor.RED) + state.getGrowth());
        }
        if (state.getMultiplier() != 1) {
            ib.lore(ChatColor.BLUE + "Multiplier: " + state.getMultiplier());
        }
        return ib;
    }

    @Override
    public void addClickableLore(Player player) {
        base.lore(ChatColor.RED + "Click to delete");
    }

    @Override
    public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {
        try {
            group.removeBlockState(state);
        } catch (BlockNotInListException e) {
            CMRLogger.error("The GUI seems to have desynchronized from the current block list, please report this error!");
            e.printStackTrace(); // ???
            return;
        }
        holder.updateGUI();
    }

}
