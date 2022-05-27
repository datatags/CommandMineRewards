package me.datatags.commandminerewards.gui.conversations;

import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public abstract class CMRPrompt extends ValidatingPrompt {
    public abstract CMRPermission getPermission();
    private Class<?> inputClass;
    public CMRPrompt(Class<?> inputClass) {
        this.inputClass = inputClass;
    }
    @Override
    protected boolean isInputValid(ConversationContext cc, String input) {
        Player player = (Player)cc.getForWhom();
        if (!getPermission().attempt(player)) {
            Bukkit.getLogger().warning("[CommandMineRewards] GUI permissions error on user " + player.getName() + ", please report this.");
            return false;
        }
        if (inputClass == Double.class) {
            try {
                Double.parseDouble(input);
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (inputClass == Integer.class) {
            try {
                Integer.parseInt(input);
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (inputClass == String.class) {
            return true;
        } else {
            throw new IllegalArgumentException("Invalid input class type");
        }
        return true;
    }
    public abstract CMRGUI getNextGUI(ConversationContext cc);
    public abstract boolean isRewardInUse(String group, String reward);
}
