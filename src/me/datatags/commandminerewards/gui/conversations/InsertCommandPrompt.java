package me.datatags.commandminerewards.gui.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.gui.guis.CMRGUI;
import me.datatags.commandminerewards.gui.guis.RewardGUI;

public class InsertCommandPrompt extends CMRPrompt {
	private Reward reward;
	private int index;
	public InsertCommandPrompt(Reward reward, int index) {
		super(String.class);
		this.reward = reward;
		this.index = index;
	}
	@Override
	public String getPromptText(ConversationContext cc) {
		return "Please type the command to insert, without the slash. Use %player% in place of the player receiving the reward.";
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.COMMAND_MODIFY;
	}
	@Override
	public CMRGUI getNextGUI(ConversationContext cc) {
		return new RewardGUI(reward.getParent(), reward);
	}
	@Override
	protected Prompt acceptValidatedInput(ConversationContext cc, String input) {
		reward.insertCommand(input, index);
		return END_OF_CONVERSATION;
	}

}
