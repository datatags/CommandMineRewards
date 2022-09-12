package me.datatags.commandminerewards.gui.buttons.rewardgroup;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRBlockState;
import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.guis.BlockListGUI;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class BlockListButton extends GUIButton {
    private RewardGroup group;

    public BlockListButton(RewardGroup group) {
        this.group = group;
    }

    @Override
    public CMRPermission getPermission() {
        return CMRPermission.BLOCK;
    }

    @Override
    public CMRPermission getClickPermission() {
        return CMRPermission.BLOCK; // you can still view the block list without modify permission
    }

    @Override
    protected ItemBuilder build() {
        ItemBuilder ib = new ItemBuilder(Material.GRASS_BLOCK).name(ChatColor.GOLD + "Block List");
        int i = 0;
        for (CMRBlockState state : group.getBlocks()) {
            ib.lore(state.compact());
            if (++i >= 10 && group.getBlocks().size() > 10) {
                ib.lore("...and " + (group.getBlocks().size() - 10) + " more...");
                break;
            }
        }
        return ib;
    }

    @Override
    public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {
        parent.getGUIManager().delayOpenGUI(holder, new BlockListGUI(group));
    }

}
