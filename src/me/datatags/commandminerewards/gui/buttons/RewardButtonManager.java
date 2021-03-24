package me.datatags.commandminerewards.gui.buttons;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.datatags.commandminerewards.CMRBlockManager;
import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.RGCacheListener;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.buttons.main.RewardSectionButton;
import me.datatags.commandminerewards.gui.buttons.rewardgroup.RewardButton;

public class RewardButtonManager implements RGCacheListener {
	private static RewardButtonManager instance;
	private List<RewardSectionButton> sectionCache = new ArrayList<>();
	private Map<String,List<RewardButton>> rewardCache = new HashMap<>();
	private RewardButtonManager() {
		reloadCache();
		registerCacheListener();
	}
	public static RewardButtonManager getInstance() {
		if (instance == null) {
			instance = new RewardButtonManager();
		}
		return instance;
	}
	private void sort() {
		sectionCache.sort(Comparator.comparing(b -> b.getRewardGroup().getName()));
	}
	@Override
	public void reloadCache() {
		sectionCache.clear();
		rewardCache.clear();
		for (RewardGroup group : CMRBlockManager.getInstance().getGroupCache()) {
			sectionCache.add(new RewardSectionButton(group));
			loadChildren(group);
		}
		sort();
	}
	private void loadChildren(RewardGroup group) {
		List<RewardButton> buttons = new ArrayList<>();
		for (Reward reward : group.getChildren()) {
			buttons.add(new RewardButton(reward));
		}
		buttons.sort(Comparator.comparing(r -> r.getReward().getName()));
		rewardCache.put(group.getName(), buttons);
	}
	@Override
	public void unloadSection(String remove) {
		RewardSectionButton removeButton = findSectionButton(remove);
		if (removeButton == null) return;
		sectionCache.remove(removeButton); // removing something does not unsort the list
	}
	@Override
	public void reloadGroup(RewardGroup reload) {
		RewardSectionButton reloadButton = findSectionButton(reload.getName());
		if (reloadButton == null) return;
		reloadButton.resetBase();
	}
	private RewardSectionButton findSectionButton(String sectionName) {
		RewardSectionButton rsb = null;
		for (RewardSectionButton button : sectionCache) {
			if (button.getRewardGroup().getName().equals(sectionName)) {
				rsb = button;
				break;
			}
		}
		if (rsb == null) {
			CommandMineRewards.getInstance().getLogger().warning("Asked to adjust nonexistent item in GUI rg cache!");
		}
		return rsb;
	}
	@Override
	public void loadGroup(RewardGroup group) {
		sectionCache.add(new RewardSectionButton(group));
		sort();
	}
	public List<RewardSectionButton> getSectionCache() {
		return sectionCache;
	}
	public List<RewardButton> getRewardCache(RewardGroup group) {
		return rewardCache.computeIfAbsent(group.getName(), k -> new ArrayList<>());
	}
	@Override
	public void loadReward(RewardGroup group, Reward reward) {
		List<RewardButton> buttons = rewardCache.computeIfAbsent(group.getName(), k -> new ArrayList<>());
		buttons.add(new RewardButton(reward));
		buttons.sort(Comparator.comparing(r -> r.getReward().getName()));
	}
	@Override
	public void unloadReward(RewardGroup group, String rewardName) {
		RewardButton toRemove = findRewardButton(group, rewardName);
		if (toRemove == null) return;
		rewardCache.get(group.getName()).remove(toRemove);
	}
	@Override
	public void reloadReward(RewardGroup group, Reward reward) {
		RewardButton toReload = findRewardButton(group, reward.getName());
		if (toReload == null) return;
		toReload.resetBase();
	}
	private RewardButton findRewardButton(RewardGroup group, String rewardName) {
		RewardButton foundButton = null;
		for (RewardButton rb : rewardCache.get(group.getName())) {
			if (rb.getReward().getName().equals(rewardName)) {
				foundButton = rb;
				break;
			}
		}
		if (foundButton == null) {
			CommandMineRewards.getInstance().getLogger().warning("Asked to adjust nonexistent item in GUI reward cache!");
		}
		return foundButton;
	}
}
