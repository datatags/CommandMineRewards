package me.Datatags.CommandMineRewards.gui.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.Reward;
import me.Datatags.CommandMineRewards.gui.GUIManager;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;
import me.Datatags.CommandMineRewards.gui.guis.RewardGUI;

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
		return "Please type the command to insert, without the slash";
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.COMMAND_MODIFY;
	}
	@Override
	public CMRGUI getNextGUI(ConversationContext cc) {
		return GUIManager.getInstance().getGUI(RewardGUI.class, reward.getParent(), reward);
	}
	@Override
	protected Prompt acceptValidatedInput(ConversationContext cc, String input) {
		reward.insertCommand(input, index);
		return END_OF_CONVERSATION;
	}

}
