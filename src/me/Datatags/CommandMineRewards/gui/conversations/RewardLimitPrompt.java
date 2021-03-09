package me.Datatags.CommandMineRewards.gui.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.gui.GUIManager;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;
import me.Datatags.CommandMineRewards.gui.guis.MainGUI;
import me.Datatags.CommandMineRewards.gui.guis.RewardSectionGUI;

public class RewardLimitPrompt extends CMRPrompt {
	private RewardSection section;
	public RewardLimitPrompt(RewardSection section) {
		super(Integer.class);
		this.section = section;
	}

	@Override
	public String getPromptText(ConversationContext cc) {
		int value;
		if (section == null) {
			value = ((GlobalConfigManager)cc.getSessionData("gcm")).getGlobalRewardLimit();
		} else {
			value = section.getRewardLimit();
		}
		return "Please enter new reward limit for " + (section == null ? "global" : section.getName()) + ", current value: " + value;
	}

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.LIMIT_MODIFY;
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext cc, String input) {
		int value = Integer.parseInt(input);
		if (section == null) {
			((GlobalConfigManager)cc.getSessionData("gcm")).setGlobalRewardLimit(value);
		} else {
			section.setRewardLimit(value);
		}
		return Prompt.END_OF_CONVERSATION;
	}

	@Override
	public CMRGUI getNextGUI(ConversationContext cc) {
		GUIManager gm = getGUIManager();
		if (section == null) {
			return gm.getGUI(MainGUI.class, null, null);
		} else {
			return gm.getGUI(RewardSectionGUI.class, section, null);
		}
	}
}
