package me.datatags.commandminerewards.gui.buttons.reward;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.conversations.CMRConversationFactory;
import me.datatags.commandminerewards.gui.conversations.RewardChancePrompt;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class ChanceButton extends GUIButton {
    private RewardGroup group;
    private Reward reward;
    public ChanceButton(RewardGroup group, Reward reward) {
        this.group = group;
        this.reward = reward;
    }
    
    @Override
    public CMRPermission getPermission() {
        return CMRPermission.REWARD;
    }

    @Override
    public CMRPermission getClickPermission() {
        return CMRPermission.REWARD_MODIFY;
    }

    @Override
    protected ItemBuilder build() {
        ItemBuilder ib = new ItemBuilder(Material.GOLD_INGOT).name(ChatColor.GREEN + "Chance");
        ib.lore(ChatColor.DARK_GREEN + "Base chance: " + reward.getRawChance() + "%");
        if (reward.getRawChance() != reward.getChance()) {
            ib.lore(ChatColor.BLUE + "Chance adjusted by multiplier: " + reward.getChance() + "%");
        }
        return ib;
    }
    
    @Override
    public void addClickableLore(Player player) {
        base.lore(ChatColor.YELLOW + "Click to modify");
    }
    
    @Override
    public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {
        CMRConversationFactory.startConversation(holder, new RewardChancePrompt(group, reward));
    }

}
