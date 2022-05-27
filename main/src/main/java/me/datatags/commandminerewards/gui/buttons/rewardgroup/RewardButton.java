package me.datatags.commandminerewards.gui.buttons.rewardgroup;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.commands.RewardCommandEntry;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.guis.CMRGUI;
import me.datatags.commandminerewards.gui.guis.RewardGUI;

public class RewardButton extends GUIButton {
    private Reward reward;
    public RewardButton(Reward reward) {
        this.reward = reward;
    }

    @Override
    public CMRPermission getPermission() {
        return CMRPermission.REWARD;
    }

    @Override
    public CMRPermission getClickPermission() {
        return CMRPermission.REWARD;
    }

    @Override
    protected ItemBuilder build() {
        ItemBuilder ib = new ItemBuilder(Material.BOOK).name(ChatColor.LIGHT_PURPLE + reward.getName());
        ib.lore(ChatColor.BLUE + "Active chance: " + reward.getChance() + "%");
        ib.lore(ChatColor.LIGHT_PURPLE + "Commands:");
        if (hasCommands()) {
            int i = 0;
            int howMany = reward.getCommands().size();
            for (RewardCommandEntry cmd : reward.getCommands()) {
                ib.lore(ChatColor.LIGHT_PURPLE + "- " + cmd.getCommand());
                if (++i > 10 && howMany > 10) {
                    ib.lore(ChatColor.LIGHT_PURPLE + "...and " + (howMany - 10) + " more...");
                    break;
                }
            }
        } else {
            ib.lore(ChatColor.RED + "- None");
        }
        return ib;
    }
    
    @Override
    public void addClickableLore(Player player) {
        if (CMRPermission.COMMAND_MODIFY.test(player)) {
            if (hasCommands()) {
                base.lore(ChatColor.YELLOW + "You must delete all commands under");
                base.lore(ChatColor.YELLOW + "this reward before deleting it.");
            } else {
                base.lore(ChatColor.RED + "Right-click to delete");
            }
        }
        if (CMRPermission.COMMAND_EXECUTE.test(player)) {
            base.lore(ChatColor.GREEN + "Middle-click to test all commands");
        }
    }

    @Override
    public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {
        Player owner = holder.getOwner();
        if (clickType.isRightClick() && CMRPermission.COMMAND_MODIFY.test(owner)) {
            if (hasCommands()) return;
            reward.delete();
            holder.updateGUI();
            return;
        } else if (clickType == ClickType.MIDDLE && CMRPermission.COMMAND_EXECUTE.test(owner)) {
            reward.execute(owner, true);
            return;
        }
        parent.getGUIManager().delayOpenGUI(holder, new RewardGUI(reward.getParent(), reward));
    }
    
    public Reward getReward() {
        return reward;
    }
    
    private boolean hasCommands() {
        return reward.getCommands().size() > 0;
    }

}
