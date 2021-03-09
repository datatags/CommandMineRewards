package me.Datatags.CommandMineRewards.gui.conversations;

import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.entity.Player;

public class AbandonListener implements ConversationAbandonedListener {
	@Override
	public void conversationAbandoned(ConversationAbandonedEvent e) {
		if (!e.gracefulExit()) return;
		Player player = (Player)e.getContext().getForWhom();
		CMRPrompt prompt = (CMRPrompt) e.getContext().getSessionData("prompt");
		prompt.getNextGUI(e.getContext()).openFor(player);
	}
	
}
