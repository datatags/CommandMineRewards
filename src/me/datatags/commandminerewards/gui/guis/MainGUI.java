package me.datatags.commandminerewards.gui.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.RGCacheListener;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.buttons.RewardButtonManager;
import me.datatags.commandminerewards.gui.buttons.area.RegionListButton;
import me.datatags.commandminerewards.gui.buttons.area.WorldListButton;
import me.datatags.commandminerewards.gui.buttons.general.FillerButton;
import me.datatags.commandminerewards.gui.buttons.general.NewRewardButton;
import me.datatags.commandminerewards.gui.buttons.general.RewardLimitButton;
import me.datatags.commandminerewards.gui.buttons.general.SilkTouchButton;
import me.datatags.commandminerewards.gui.buttons.main.MultiplierButton;
import me.datatags.commandminerewards.gui.buttons.paginated.NextPageButton;
import me.datatags.commandminerewards.gui.buttons.paginated.PreviousPageButton;

public class MainGUI extends PaginatedGUI implements RGCacheListener {
	private RewardButtonManager rbm;
	private NewRewardButton newSectionButton;
	public MainGUI() {
		rbm = RewardButtonManager.getInstance();
		gui[0][0] = new MultiplierButton();
		gui[0][2] = new SilkTouchButton(null, null);
		gui[0][4] = new RewardLimitButton(null);
		gui[0][6] = new WorldListButton(null);
		gui[0][8] = new RegionListButton(null);
		for (int i = 0; i < 9; i++) {
			gui[1][i] = new FillerButton();
		}
		gui[5][0] = new PreviousPageButton();
		gui[5][8] = new NextPageButton();
		this.newSectionButton = new NewRewardButton(null);
	}
	@Override
	public String getTitle() {
		return ChatColor.GOLD + "CommandMineRewards v" + CommandMineRewards.getInstance().getDescription().getVersion();
	}
	@Override
	public int getMaxPages() {
		// 27 items to a page. 27d because we don't want integer division.
		return (int)Math.ceil((rbm.getSectionCache().size() + 1) / 27d); // add one for the new group button
	}
	@Override
	public GUIButton[][] getPage(int pageN) {
		List<GUIButton> buttonCache = new ArrayList<>(rbm.getSectionCache());
		buttonCache.add(0, newSectionButton);
		return generatePage(pageN, 2, 27, buttonCache);
	}
	@Override
	public CMRGUI getPreviousGUI() {
		return null;
	}
	
}
