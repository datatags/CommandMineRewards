package me.datatags.commandminerewards.gui.buttons.general;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.conversations.CMRConversationFactory;
import me.datatags.commandminerewards.gui.conversations.RewardLimitPrompt;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class RewardLimitButton extends GUIButton {
    private RewardGroup group;
    private GlobalConfigManager gcm;
    private int limit;
    public RewardLimitButton(RewardGroup group) {
        this.group = group;
        this.gcm = GlobalConfigManager.getInstance();
        if (group == null) {
            limit = gcm.getGlobalRewardLimit();
        } else {
            limit = group.getRewardLimit();
        }
    }
    @Override
    public CMRPermission getPermission() {
        return CMRPermission.LIMIT;
    }

    @Override
    public CMRPermission getClickPermission() {
        return CMRPermission.LIMIT_MODIFY;
    }

    @Override
    protected ItemBuilder build() {
        ItemBuilder ib = new ItemBuilder(Material.CLOCK).name(ChatColor.RED + "Reward Limit");
        String valueStr = limit + (limit == -1 ? " (No limit)" : "");
        ib.lore(ChatColor.RED + "Current limit: " + valueStr);
        return ib;
    }
    
    @Override
    public void addClickableLore(Player player) {
        if (limit > -1) {
            base.lore(ChatColor.RED + "Right-click to clear limit");
        }
    }

    @Override
    public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {
        if (clickType.isRightClick()) {
            if (group == null) {
                GlobalConfigManager.getInstance().setGlobalRewardLimit(-1);
            } else {
                group.setRewardLimit(-1);
            }
            holder.updateGUI();
            return;
        }
        CMRConversationFactory.startConversation(holder, new RewardLimitPrompt(group));
    }

}
