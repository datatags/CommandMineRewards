package me.datatags.commandminerewards.gui.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.guis.CMRGUI;
import me.datatags.commandminerewards.gui.guis.MainGUI;
import me.datatags.commandminerewards.gui.guis.RewardGroupGUI;

public class RewardLimitPrompt extends CMRPrompt {
    private RewardGroup group;
    public RewardLimitPrompt(RewardGroup group) {
        super(Integer.class);
        this.group = group;
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        int value;
        if (group == null) {
            value = ((GlobalConfigManager)cc.getSessionData("gcm")).getGlobalRewardLimit();
        } else {
            value = group.getRewardLimit();
        }
        return "Please enter new reward limit for " + (group == null ? "global" : group.getName()) + ", current value: " + value;
    }

    @Override
    public CMRPermission getPermission() {
        return CMRPermission.LIMIT_MODIFY;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String input) {
        int value = Integer.parseInt(input);
        if (group == null) {
            ((GlobalConfigManager)cc.getSessionData("gcm")).setGlobalRewardLimit(value);
        } else {
            group.setRewardLimit(value);
        }
        return Prompt.END_OF_CONVERSATION;
    }

    @Override
    public CMRGUI getNextGUI(ConversationContext cc) {
        if (group == null) {
            return new MainGUI();
        } else {
            return new RewardGroupGUI(group);
        }
    }

    @Override
    public boolean isRewardInUse(String group, String reward) {
        if (this.group == null || reward != null) return false;
        return this.group.getName().equals(group);
    }
}
