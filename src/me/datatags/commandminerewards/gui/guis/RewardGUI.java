package me.datatags.commandminerewards.gui.guis;

import java.util.ArrayList;
import java.util.List;

import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.commands.RewardCommandEntry;
import me.datatags.commandminerewards.gui.buttons.general.SilkTouchButton;
import me.datatags.commandminerewards.gui.buttons.reward.ChanceButton;
import me.datatags.commandminerewards.gui.buttons.reward.CommandButton;

public class RewardGUI extends PaginatedGUI {
	private RewardGroup group;
	private Reward reward;
	private List<CommandButton> commands = new ArrayList<>();
	public RewardGUI(RewardGroup group, Reward reward) {
		this.group = group;
		this.reward = reward;
		gui[0][3] = new ChanceButton(group, reward);
		gui[0][5] = new SilkTouchButton(group, reward);
		for (RewardCommandEntry entry : reward.getCommands()) {
			commands.add(new CommandButton(reward, entry));
		}
		if (commands.size() == 0) {
			commands.add(new CommandButton(reward, null));
		}
	}
	
	@Override
	public RewardGUI clone() {
		return new RewardGUI(group, reward);
	}
	
	@Override
	public String getTitle() {
		return "Reward - " + reward.getName();
	}

	@Override
	public CMRGUI getPreviousGUI() {
		return new RewardGroupGUI(group);
	}
	@Override
	public int getMaxPages() {
		return (int)Math.max(Math.ceil(reward.getCommands().size() / 36d), 1);
	}
	@Override
	public void preparePage(int pageN) {
		generatePage(pageN, 1, 36, commands);
	}

}
