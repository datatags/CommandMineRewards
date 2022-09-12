package me.datatags.commandminerewards.gui.buttons.main;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRBlockState;
import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.guis.CMRGUI;
import me.datatags.commandminerewards.gui.guis.RewardGroupGUI;

public class RewardGroupButton extends GUIButton {
    private RewardGroup group;

    public RewardGroupButton(RewardGroup group) {
        this.group = group;
    }

    @Override
    public CMRPermission getPermission() {
        return CMRPermission.REWARD;
    }

    @Override
    public CMRPermission getClickPermission() {
        return CMRPermission.REWARD; // not REWARD_MODIFY because clicking the button does not modify the reward group
    }

    @Override
    protected ItemBuilder build() {
        Material mat = Material.BOOKSHELF;
        if (group.getBlockTypes().size() > 0) {
            for (String block : group.getBlockTypes()) {
                Material test = Material.matchMaterial(block);
                if (test != null && test.isItem()) {
                    mat = test;
                    break;
                }
            }
        }
        ItemBuilder ib = new ItemBuilder(mat).name(ChatColor.BLUE + group.getName());
        ib.lore(ChatColor.GREEN + "Rewards:");
        if (group.getChildren().size() == 0) {
            ib.lore(ChatColor.RED + "- None");
        } else {
            int i = 0;
            int children = group.getChildrenNames().size();
            for (String child : group.getChildrenNames()) {
                ib.lore(ChatColor.DARK_GREEN + "- " + child);
                if (++i >= 10 && children > 10) {
                    ib.lore("...and " + (children - 10) + " more...");
                    break;
                }
            }
        }
        ib.lore("");
        ib.lore(ChatColor.BLUE + "Blocks:");
        int i = 0;
        if (group.getBlockTypes().size() == 0) {
            ib.lore(ChatColor.RED + "- None");
        } else {
            for (CMRBlockState state : group.getBlocks()) {
                ib.lore(ChatColor.DARK_BLUE + "- " + state.compact());
                if (++i >= 10 && group.getBlocks().size() > 10) { // loop breaks anyway if second condition fails
                    ib.lore(ChatColor.BLUE + "... and " + (group.getBlocks().size() - 10) + " more ...");
                    break;
                }
            }
        }
        return ib;
    }

    @Override
    public void addClickableLore(Player player) {
        if (!CMRPermission.REWARD_MODIFY.test(player)) return;
        if (group.getChildrenNames().size() == 0) {
            base.lore(ChatColor.RED + "Right-click to delete");
        } else {
            base.lore(ChatColor.YELLOW + "You must delete all rewards under");
            base.lore(ChatColor.YELLOW + "this group before deleting it.");
        }
    }

    @Override
    public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {
        if (clickType.isLeftClick()) {
            parent.getGUIManager().delayOpenGUI(holder, new RewardGroupGUI(group));
        } else if (CMRPermission.REWARD_MODIFY.test(holder.getOwner()) && clickType.isRightClick() && group.getChildrenNames().size() == 0) {
            group.delete();
            holder.updateGUI();
        }
    }

    public RewardGroup getRewardGroup() {
        return group;
    }

}
