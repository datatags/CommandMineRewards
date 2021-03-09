package me.Datatags.CommandMineRewards.gui.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import me.Datatags.CommandMineRewards.RSCacheListener;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.buttons.RewardButtonManager;
import me.Datatags.CommandMineRewards.gui.buttons.area.RegionListButton;
import me.Datatags.CommandMineRewards.gui.buttons.area.WorldListButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.NewRewardButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.RewardLimitButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.SilkTouchButton;
import me.Datatags.CommandMineRewards.gui.buttons.rewardsection.BlockListButton;

public class RewardSectionGUI extends PaginatedGUI implements RSCacheListener {
	private RewardSection section;
	private RewardButtonManager rbm;
	private NewRewardButton newRewardButton;
	public RewardSectionGUI(RewardSection section) {
		this.section = section;
		this.rbm = RewardButtonManager.getInstance();
		gui[0][0] = new BlockListButton(section);
		gui[0][2] = new RewardLimitButton(section);
		gui[0][4] = new SilkTouchButton(section, null);
		gui[0][6] = new WorldListButton(section);
		gui[0][8] = new RegionListButton(section);
		this.newRewardButton = new NewRewardButton(section);
		addPageButtons();
		registerCacheListener();
	}
	
	@Override
	public int getMaxPages() {
		return (int)Math.ceil((section.getChildrenNames().size() + 1) / 27d); // once again, add one for the new reward button
	}

	@Override
	public GUIButton[][] getPage(int pageN) {
		List<GUIButton> buttonCache = new ArrayList<>(rbm.getRewardCache(section));
		buttonCache.add(newRewardButton);
		return generatePage(pageN, 2, 27, buttonCache);
	}

	@Override
	public String getTitle() {
		return ChatColor.GOLD + "Reward section: " + ChatColor.BLUE + section.getName();
	}

	@Override
	public CMRGUI getPreviousGUI() {
		return getGUIManager().getGUI(MainGUI.class, null, null);
	}

}
