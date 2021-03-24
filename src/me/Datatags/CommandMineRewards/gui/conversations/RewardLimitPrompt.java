package me.Datatags.CommandMineRewards.gui.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.RewardGroup;
import me.Datatags.CommandMineRewards.gui.GUIManager;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;
import me.Datatags.CommandMineRewards.gui.guis.MainGUI;
import me.Datatags.CommandMineRewards.gui.guis.RewardSectionGUI;

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
		GUIManager gm = getGUIManager();
		if (group == null) {
			return gm.getGUI(MainGUI.class, null, null);
		} else {
			return gm.getGUI(RewardSectionGUI.class, group, null);
		}
	}
}
