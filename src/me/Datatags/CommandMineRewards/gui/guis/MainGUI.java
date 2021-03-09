package me.Datatags.CommandMineRewards.gui.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import me.Datatags.CommandMineRewards.CommandMineRewards;
import me.Datatags.CommandMineRewards.RSCacheListener;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.buttons.RewardButtonManager;
import me.Datatags.CommandMineRewards.gui.buttons.area.RegionListButton;
import me.Datatags.CommandMineRewards.gui.buttons.area.WorldListButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.FillerButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.NewRewardButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.RewardLimitButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.SilkTouchButton;
import me.Datatags.CommandMineRewards.gui.buttons.main.MultiplierButton;
import me.Datatags.CommandMineRewards.gui.buttons.paginated.NextPageButton;
import me.Datatags.CommandMineRewards.gui.buttons.paginated.PreviousPageButton;

public class MainGUI extends PaginatedGUI implements RSCacheListener {
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
		return (int)Math.ceil((rbm.getSectionCache().size() + 1) / 27d); // add one for the new section button
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
