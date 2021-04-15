package me.datatags.commandminerewards.gui.conversations;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.InvalidRewardException;
import me.datatags.commandminerewards.Exceptions.InvalidRewardGroupException;
import me.datatags.commandminerewards.Exceptions.RewardAlreadyExistsException;
import me.datatags.commandminerewards.Exceptions.RewardGroupAlreadyExistsException;
import me.datatags.commandminerewards.gui.guis.CMRGUI;
import me.datatags.commandminerewards.gui.guis.RewardGUI;
import me.datatags.commandminerewards.gui.guis.RewardGroupGUI;

public class RewardNamePrompt extends CMRPrompt {
	private RewardGroup group;
	public RewardNamePrompt(RewardGroup group) {
		super(String.class);
		this.group = group;
	}
	@Override
	public String getPromptText(ConversationContext cc) {
		return "Please enter new reward " + (group == null ? "group " : "") + "name.";
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.REWARD_MODIFY;
	}
	@Override
	protected Prompt acceptValidatedInput(ConversationContext cc, String input) {
		Player player = (Player)cc.getForWhom();
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
		if (group == null) {
			return new RewardGroupGUI((RewardGroup) cc.getSessionData("group"));
		} else {
			return new RewardGUI(group, (Reward) cc.getSessionData("reward"));
		}
	}
	@Override
	public boolean isRewardInUse(String group, String reward) {
		if (this.group == null || reward != null) return false;
		return this.group.getName().equals(group);
	}

}
