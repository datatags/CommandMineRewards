package me.Datatags.CommandMineRewards.gui.buttons;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.Datatags.CommandMineRewards.CMRBlockManager;
import me.Datatags.CommandMineRewards.CommandMineRewards;
import me.Datatags.CommandMineRewards.RSCacheListener;
import me.Datatags.CommandMineRewards.Reward;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.gui.buttons.main.RewardSectionButton;
import me.Datatags.CommandMineRewards.gui.buttons.rewardsection.RewardButton;

public class RewardButtonManager implements RSCacheListener {
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
		sectionCache.sort(Comparator.comparing(b -> b.getRewardSection().getName()));
	}
	@Override
	public void reloadCache() {
		sectionCache.clear();
		rewardCache.clear();
		for (RewardSection section : CMRBlockManager.getInstance().getSectionCache()) {
			sectionCache.add(new RewardSectionButton(section));
			loadChildren(section);
		}
		sort();
	}
	private void loadChildren(RewardSection section) {
		List<RewardButton> buttons = new ArrayList<>();
		for (Reward reward : section.getChildren()) {
			buttons.add(new RewardButton(reward));
		}
		buttons.sort(Comparator.comparing(r -> r.getReward().getName()));
		rewardCache.put(section.getName(), buttons);
	}
	@Override
	public void unloadSection(String remove) {
		RewardSectionButton removeButton = findSectionButton(remove);
		if (removeButton == null) return;
		sectionCache.remove(removeButton); // removing something does not unsort the list
	}
	@Override
	public void reloadSection(RewardSection reload) {
		RewardSectionButton reloadButton = findSectionButton(reload.getName());
		if (reloadButton == null) return;
		reloadButton.resetBase();
	}
	private RewardSectionButton findSectionButton(String sectionName) {
		RewardSectionButton rsb = null;
		for (RewardSectionButton button : sectionCache) {
			if (button.getRewardSection().getName().equals(sectionName)) {
				rsb = button;
				break;
			}
		}
		if (rsb == null) {
			CommandMineRewards.getInstance().getLogger().warning("Asked to adjust nonexistent item in GUI RS cache!");
		}
		return rsb;
	}
	@Override
	public void loadSection(RewardSection section) {
		sectionCache.add(new RewardSectionButton(section));
		sort();
	}
	public List<RewardSectionButton> getSectionCache() {
		return sectionCache;
	}
	public List<RewardButton> getRewardCache(RewardSection section) {
		return rewardCache.computeIfAbsent(section.getName(), k -> new ArrayList<>());
	}
	@Override
	public void loadReward(RewardSection section, Reward reward) {
		List<RewardButton> buttons = rewardCache.computeIfAbsent(section.getName(), k -> new ArrayList<>());
		buttons.add(new RewardButton(reward));
		buttons.sort(Comparator.comparing(r -> r.getReward().getName()));
	}
	@Override
	public void unloadReward(RewardSection section, String rewardName) {
		RewardButton toRemove = findRewardButton(section, rewardName);
		if (toRemove == null) return;
		rewardCache.get(section.getName()).remove(toRemove);
	}
	@Override
	public void reloadReward(RewardSection section, Reward reward) {
		RewardButton toReload = findRewardButton(section, reward.getName());
		if (toReload == null) return;
		toReload.resetBase();
	}
	private RewardButton findRewardButton(RewardSection section, String rewardName) {
		RewardButton foundButton = null;
		for (RewardButton rb : rewardCache.get(section.getName())) {
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
