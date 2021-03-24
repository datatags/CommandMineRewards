package me.datatags.commandminerewards.gui.guis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import me.datatags.commandminerewards.RGCacheListener;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.buttons.RewardButtonManager;
import me.datatags.commandminerewards.gui.buttons.area.RegionListButton;
import me.datatags.commandminerewards.gui.buttons.area.WorldListButton;
import me.datatags.commandminerewards.gui.buttons.general.NewRewardButton;
import me.datatags.commandminerewards.gui.buttons.general.RewardLimitButton;
import me.datatags.commandminerewards.gui.buttons.general.SilkTouchButton;
import me.datatags.commandminerewards.gui.buttons.rewardgroup.BlockListButton;

public class RewardGroupGUI extends PaginatedGUI implements RGCacheListener {
	private RewardGroup group;
	private RewardButtonManager rbm;
	private NewRewardButton newRewardButton;
	public RewardGroupGUI(RewardGroup group) {
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
		buttonCache.add(0, newRewardButton);
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
