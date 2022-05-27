package me.datatags.commandminerewards.gui.conversations;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.gui.GUIManager;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class AbandonListener implements ConversationAbandonedListener {
    @Override
    public void conversationAbandoned(ConversationAbandonedEvent e) {
        Player player = (Player)e.getContext().getForWhom();
        GUIUserHolder holder = (GUIUserHolder) e.getContext().getSessionData("userholder");
        if (!e.gracefulExit()) {
            player.sendMessage(ChatColor.RED + "Cancelled.");
            for (UUID helper : holder.getHelpers()) {
                Bukkit.getPlayer(helper).sendMessage(ChatColor.RED + player.getName() + " has aborted the conversation.");
            }
            GUIManager.getInstance().removeUser(player);
            return;
        }
        CMRPrompt prompt = (CMRPrompt) e.getContext().getSessionData("prompt");
        CMRGUI gui = prompt.getNextGUI(e.getContext());
        gui.openFor(holder);
        GUIManager.getInstance().refreshAllExcept(holder);
    }
    
}
