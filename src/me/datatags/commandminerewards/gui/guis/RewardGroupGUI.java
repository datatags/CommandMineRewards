package me.datatags.commandminerewards.gui.guis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;

import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.buttons.area.RegionListButton;
import me.datatags.commandminerewards.gui.buttons.area.WorldListButton;
import me.datatags.commandminerewards.gui.buttons.general.NewRewardButton;
import me.datatags.commandminerewards.gui.buttons.general.RewardLimitButton;
import me.datatags.commandminerewards.gui.buttons.general.SilkTouchButton;
import me.datatags.commandminerewards.gui.buttons.rewardgroup.BlockListButton;
import me.datatags.commandminerewards.gui.buttons.rewardgroup.RewardButton;

public class RewardGroupGUI extends PaginatedGUI {
	private RewardGroup group;
	private List<GUIButton> rewardButtons;
	public RewardGroupGUI(RewardGroup group) {
		this.group = group;
		gui[0][0] = new BlockListButton(group);
		gui[0][2] = new RewardLimitButton(group);
		gui[0][4] = new SilkTouchButton(group, null);
		gui[0][6] = new WorldListButton(group);
		gui[0][8] = new RegionListButton(group);
		addPageButtons();
		List<RewardButton> buttons = new ArrayList<>();
		for (Reward reward : group.getChildren()) {
			buttons.add(new RewardButton(reward));
		}
		buttons.sort(Comparator.comparing(r -> r.getReward().getName()));
		rewardButtons = new ArrayList<>(buttons);
		rewardButtons.add(0, new NewRewardButton(group));
	}
	
	@Override
	public RewardGroupGUI clone() {
		return new RewardGroupGUI(group);
	}
	
	@Override
	public int getMaxPages() {
		return (int)Math.ceil((group.getChildrenNames().size() + 1) / 27d); // once again, add one for the new reward button
	}

	@Override
	public void preparePage(int pageN) {
		generatePage(pageN, 2, 27, rewardButtons);
	}

	@Override
	public String getTitle() {
		return ChatColor.GOLD + "Reward group: " + ChatColor.BLUE + group.getName();
	}

	@Override
	public CMRGUI getPreviousGUI() {
		return new MainGUI();
	}

}