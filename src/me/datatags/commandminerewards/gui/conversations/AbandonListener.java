package me.datatags.commandminerewards.gui.conversations;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.entity.Player;

public class AbandonListener implements ConversationAbandonedListener {
	@Override
	public void conversationAbandoned(ConversationAbandonedEvent e) {
		Player player = (Player)e.getContext().getForWhom();
		if (!e.gracefulExit()) {
			player.sendMessage(ChatColor.RED + "Cancelled.");
			return;
		}
		CMRPrompt prompt = (CMRPrompt) e.getContext().getSessionData("prompt");
		prompt.getNextGUI(e.getContext()).openFor(player);
	}
	
}
