package me.datatags.commandminerewards.gui.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.guis.CMRGUI;
import me.datatags.commandminerewards.gui.guis.RewardGUI;

public class RewardChancePrompt extends CMRPrompt {
	private RewardGroup group;
	private Reward reward;
	public RewardChancePrompt(RewardGroup group, Reward reward) {
		super(Double.class);
		this.group = group;
		this.reward = reward;
	}

	@Override
	public String getPromptText(ConversationContext cc) {
		return "Enter new base chance for reward, current value: " + reward.getRawChance() + "% (omit % sign)";
	}

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.REWARD_MODIFY;
	}

	@Override
	public CMRGUI getNextGUI(ConversationContext cc) {
		return new RewardGUI(group, reward);
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext cc, String input) {
		reward.setChance(Double.parseDouble(input));
		return END_OF_CONVERSATION;
	}

	@Override
	public boolean isRewardInUse(String group, String reward) {
		if (!this.group.getName().equals(group)) return false;
		return this.reward.getName().equals(reward);
	}
	
}
