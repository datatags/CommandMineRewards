package me.datatags.commandminerewards.gui.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.gui.guis.CMRGUI;
import me.datatags.commandminerewards.gui.guis.MainGUI;

public class MultiplierPrompt extends CMRPrompt {
	public MultiplierPrompt() {
		super(Double.class);
	}
	@Override
	public String getPromptText(ConversationContext cc) {
		return "Please enter new multipler value. Current value: " + GlobalConfigManager.getInstance().getMultiplier() + ", Default: 1.0";
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.MULTIPLIER_MODIFY;
	}
	@Override
	protected Prompt acceptValidatedInput(ConversationContext cc, String input) {
		GlobalConfigManager.getInstance().setMultiplier(Double.parseDouble(input));
		return END_OF_CONVERSATION;
	}
	@Override
	public CMRGUI getNextGUI(ConversationContext cc) {
		return getGUIManager().getGUI(MainGUI.class, null, null);
	}
}
