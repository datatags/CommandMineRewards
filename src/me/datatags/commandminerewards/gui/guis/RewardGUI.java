package me.datatags.commandminerewards.gui.guis;

import java.util.ArrayList;
import java.util.List;

import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.commands.RewardCommandEntry;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.buttons.general.SilkTouchButton;
import me.datatags.commandminerewards.gui.buttons.misc.CommandButton;

public class RewardGUI extends PaginatedGUI {
	private RewardGroup group;
	private Reward reward;
	private List<CommandButton> commands = new ArrayList<>();
	public RewardGUI(RewardGroup group, Reward reward) {
		this.group = group;
		this.reward = reward;
		gui[0][4] = new SilkTouchButton(group, reward);
		for (RewardCommandEntry entry : reward.getCommands()) {
			commands.add(new CommandButton(reward, entry));
		}
	}
	@Override
	public String getTitle() {
		return "Reward - " + reward.getName();
	}

	@Override
	public CMRGUI getPreviousGUI() {
		return getGUIManager().getGUI(RewardSectionGUI.class, group, null);
	}
	@Override
	public int getMaxPages() {
		return (int)Math.ceil(reward.getCommands().size() / 36d);
	}
	@Override
	public GUIButton[][] getPage(int pageN) {
		return this.generatePage(pageN, 1, 36, commands);
	}

}
