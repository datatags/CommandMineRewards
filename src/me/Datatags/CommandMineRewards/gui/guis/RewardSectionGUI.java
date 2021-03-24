package me.Datatags.CommandMineRewards.gui.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import me.Datatags.CommandMineRewards.RGCacheListener;
import me.Datatags.CommandMineRewards.RewardGroup;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.buttons.RewardButtonManager;
import me.Datatags.CommandMineRewards.gui.buttons.area.RegionListButton;
import me.Datatags.CommandMineRewards.gui.buttons.area.WorldListButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.NewRewardButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.RewardLimitButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.SilkTouchButton;
import me.Datatags.CommandMineRewards.gui.buttons.rewardgroup.BlockListButton;

public class RewardSectionGUI extends PaginatedGUI implements RGCacheListener {
	private RewardGroup group;
	private RewardButtonManager rbm;
	private NewRewardButton newRewardButton;
	public RewardSectionGUI(RewardGroup group) {
		this.group = group;
		this.rbm = RewardButtonManager.getInstance();
		gui[0][0] = new BlockListButton(group);
		gui[0][2] = new RewardLimitButton(group);
		gui[0][4] = new SilkTouchButton(group, null);
		gui[0][6] = new WorldListButton(group);
		gui[0][8] = new RegionListButton(group);
		this.newRewardButton = new NewRewardButton(group);
		addPageButtons();
		registerCacheListener();
	}
	
	@Override
	public int getMaxPages() {
		return (int)Math.ceil((group.getChildrenNames().size() + 1) / 27d); // once again, add one for the new reward button
	}

	@Override
	public GUIButton[][] getPage(int pageN) {
		List<GUIButton> buttonCache = new ArrayList<>(rbm.getRewardCache(group));
		buttonCache.add(newRewardButton);
		return generatePage(pageN, 2, 27, buttonCache);
	}

	@Override
	public String getTitle() {
		return ChatColor.GOLD + "Reward group: " + ChatColor.BLUE + group.getName();
	}

	@Override
	public CMRGUI getPreviousGUI() {
		return getGUIManager().getGUI(MainGUI.class, null, null);
	}

}
