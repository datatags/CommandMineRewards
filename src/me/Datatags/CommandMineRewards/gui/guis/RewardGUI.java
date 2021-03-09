package me.Datatags.CommandMineRewards.gui.guis;

import java.util.ArrayList;
import java.util.List;

import me.Datatags.CommandMineRewards.Reward;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.commands.RewardCommandEntry;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.SilkTouchButton;
import me.Datatags.CommandMineRewards.gui.buttons.misc.CommandButton;

public class RewardGUI extends PaginatedGUI {
	private RewardSection section;
	private Reward reward;
	private List<CommandButton> commands = new ArrayList<>();
	public RewardGUI(RewardSection section, Reward reward) {
		this.section = section;
		this.reward = reward;
		gui[0][4] = new SilkTouchButton(section, reward);
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
		return getGUIManager().getGUI(RewardSectionGUI.class, section, null);
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
