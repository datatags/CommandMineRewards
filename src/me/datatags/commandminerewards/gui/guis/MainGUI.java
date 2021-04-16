package me.datatags.commandminerewards.gui.guis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;

import me.datatags.commandminerewards.CMRBlockManager;
import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.RGCacheListener;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.buttons.area.RegionListButton;
import me.datatags.commandminerewards.gui.buttons.area.WorldListButton;
import me.datatags.commandminerewards.gui.buttons.general.FillerButton;
import me.datatags.commandminerewards.gui.buttons.general.NewRewardButton;
import me.datatags.commandminerewards.gui.buttons.general.RewardLimitButton;
import me.datatags.commandminerewards.gui.buttons.general.SilkTouchButton;
import me.datatags.commandminerewards.gui.buttons.main.MultiplierButton;
import me.datatags.commandminerewards.gui.buttons.main.RewardGroupButton;

public class MainGUI extends PaginatedGUI implements RGCacheListener {
	private CMRBlockManager cbm;
	private List<GUIButton> groupButtons;
	public MainGUI() {
		gui[0][0] = new MultiplierButton();
		gui[0][2] = new SilkTouchButton(null, null);
		gui[0][4] = new RewardLimitButton(null);
		gui[0][6] = new WorldListButton(null);
		gui[0][8] = new RegionListButton(null);
		for (int i = 0; i < 9; i++) {
			gui[1][i] = new FillerButton();
		}
		addPageButtons();
		this.cbm = CMRBlockManager.getInstance();
		List<RewardGroupButton> buttons = new ArrayList<>();
		for (RewardGroup group : cbm.getGroupCache()) {
			buttons.add(new RewardGroupButton(group));
		}
		buttons.sort(Comparator.comparing(b -> b.getRewardGroup().getName()));
		groupButtons = new ArrayList<>(buttons);
		groupButtons.add(0, new NewRewardButton(null));
	}
	@Override
	public MainGUI getNewSelf() {
		return new MainGUI();
	}
	@Override
	public String getTitle() {
		return ChatColor.GOLD + "CommandMineRewards v" + CommandMineRewards.getInstance().getDescription().getVersion();
	}
	@Override
	public int getMaxPages() {
		// 27 items to a page. 27d because we don't want integer division.
		return (int)Math.ceil((groupButtons.size() + 1) / 27d); // add one for the new group button
	}
	@Override
	public void preparePage(int pageN) {
		generatePage(pageN, 2, 27, groupButtons);
	}
	@Override
	public CMRGUI getPreviousGUI() {
		return null;
	}
	@Override
	public boolean isRewardInUse(String group, String reward) {
		return false;
	}
	
}
