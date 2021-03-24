package me.Datatags.CommandMineRewards.gui.conversations;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.Reward;
import me.Datatags.CommandMineRewards.RewardGroup;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardGroupException;
import me.Datatags.CommandMineRewards.Exceptions.RewardAlreadyExistsException;
import me.Datatags.CommandMineRewards.Exceptions.RewardGroupAlreadyExistsException;
import me.Datatags.CommandMineRewards.gui.GUIManager;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;
import me.Datatags.CommandMineRewards.gui.guis.RewardGUI;
import me.Datatags.CommandMineRewards.gui.guis.RewardSectionGUI;

public class RewardNamePrompt extends CMRPrompt {
	private RewardGroup group;
	public RewardNamePrompt(RewardGroup group) {
		super(String.class);
		this.group = group;
	}
	@Override
	public String getPromptText(ConversationContext cc) {
		return "Please enter new reward " + (group == null ? "group " : "") + "name or 'cancel' to abort";
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.REWARD_MODIFY;
	}
	@Override
	protected Prompt acceptValidatedInput(ConversationContext cc, String input) {
		Player player = (Player)cc.getForWhom();
		if (input.equalsIgnoreCase("cancel")) {
			player.sendMessage("Aborting");
			return Prompt.END_OF_CONVERSATION;
		}
		if (group == null) {
			try {
				cc.setSessionData("group", new RewardGroup(input, true));
			} catch (InvalidRewardGroupException | RewardGroupAlreadyExistsException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
				return this;
			}
		} else {
			try {
				cc.setSessionData("reward", new Reward(group, input, true));
			} catch (InvalidRewardException | RewardAlreadyExistsException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
				return this;
			}
		}
		return Prompt.END_OF_CONVERSATION;
	}
	@Override
	public CMRGUI getNextGUI(ConversationContext cc) {
		GUIManager gm = getGUIManager();
		if (group == null) {
			return gm.getGUI(RewardSectionGUI.class, (RewardGroup) cc.getSessionData("group"), null);
		} else {
			return gm.getGUI(RewardGUI.class, group, (Reward) cc.getSessionData("reward"));
		}
	}

}
