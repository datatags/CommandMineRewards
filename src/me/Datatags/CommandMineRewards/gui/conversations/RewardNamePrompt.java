package me.Datatags.CommandMineRewards.gui.conversations;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.Reward;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidRewardSectionException;
import me.Datatags.CommandMineRewards.Exceptions.RewardAlreadyExistsException;
import me.Datatags.CommandMineRewards.Exceptions.RewardSectionAlreadyExistsException;
import me.Datatags.CommandMineRewards.gui.GUIManager;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;
import me.Datatags.CommandMineRewards.gui.guis.RewardGUI;
import me.Datatags.CommandMineRewards.gui.guis.RewardSectionGUI;

public class RewardNamePrompt extends CMRPrompt {
	private RewardSection section;
	public RewardNamePrompt(RewardSection section) {
		super(String.class);
		this.section = section;
	}
	@Override
	public String getPromptText(ConversationContext cc) {
		return "Please enter new reward " + (section == null ? "section " : "") + "name or 'cancel' to abort";
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
		if (section == null) {
			try {
				cc.setSessionData("section", new RewardSection(input, true));
			} catch (InvalidRewardSectionException | RewardSectionAlreadyExistsException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
				return this;
			}
		} else {
			try {
				cc.setSessionData("reward", new Reward(section, input, true));
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
		if (section == null) {
			return gm.getGUI(RewardSectionGUI.class, (RewardSection) cc.getSessionData("section"), null);
		} else {
			return gm.getGUI(RewardGUI.class, section, (Reward) cc.getSessionData("reward"));
		}
	}

}
