package me.datatags.commandminerewards.gui.conversations;

import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class CMRConversationFactory {
	private static ConversationFactory cf;
	static {
		cf = new ConversationFactory(CommandMineRewards.getInstance());
		cf.thatExcludesNonPlayersWithMessage(ChatColor.RED + "Only players can use the GUI");
		cf.withEscapeSequence("cancel");
		cf.withModality(false);
		cf.withLocalEcho(false);
	}
	public static void startConversation(Player player, CMRPrompt prompt) {
		CMRGUI.delayCloseGUI(player);
		Conversation convo = cf.addConversationAbandonedListener(new AbandonListener()).withFirstPrompt(prompt).buildConversation(player);
		convo.getContext().setSessionData("gcm", GlobalConfigManager.getInstance());
		convo.getContext().setSessionData("prompt", prompt); // used for AbandonListener
		player.sendMessage(ChatColor.RED + "Type 'cancel' to cancel");
		convo.begin();
	}
}
