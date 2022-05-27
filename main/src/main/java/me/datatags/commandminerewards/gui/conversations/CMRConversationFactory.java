package me.datatags.commandminerewards.gui.conversations;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.gui.GUIManager;
import me.datatags.commandminerewards.gui.GUIUserHolder;

public class CMRConversationFactory {
    private static ConversationFactory cf;
    private static GUIManager gm = GUIManager.getInstance();
    static {
        cf = new ConversationFactory(CommandMineRewards.getInstance())
            .thatExcludesNonPlayersWithMessage(ChatColor.RED + "Only players can use the GUI")
            .withEscapeSequence("cancel")
            .withModality(false)
            .withLocalEcho(false)
            .addConversationAbandonedListener(new AbandonListener());
    }
    public static void startConversation(Player player, CMRPrompt prompt) {
        startConversation(gm.getHolder(player), prompt);
    }
    public static void startConversation(GUIUserHolder holder, CMRPrompt prompt) {
        Player owner = holder.getOwner();
        Conversation convo = cf.withFirstPrompt(prompt).buildConversation(owner);
        holder.setConversation(convo);
        gm.delayCloseGUI(owner);
        for (UUID helper : holder.getHelpers()) {
            Player player = Bukkit.getPlayer(helper);
            gm.delayCloseGUI(player);
            player.sendMessage(ChatColor.YELLOW + "Please wait, " + ChatColor.GOLD + owner.getName() + ChatColor.YELLOW + " is typing a value for this prompt:");
            player.sendMessage(prompt.getPromptText(convo.getContext()));
        }
        convo.getContext().setSessionData("gcm", GlobalConfigManager.getInstance());
        convo.getContext().setSessionData("prompt", prompt); // used for AbandonListener
        owner.sendMessage(ChatColor.RED + "Type 'cancel' to cancel");
        convo.begin();
    }
}
