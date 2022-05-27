package me.datatags.commandminerewards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import me.datatags.commandminerewards.state.StateManager;

public class CMRBlockHandler {
	private Material type;
	private Boolean growth = null;
	private Set<String> rewardSections = new HashSet<>();
	private long uses = 0;
	private GlobalConfigManager gcm;
	private StateManager sm;
	protected CMRBlockHandler(RewardGroup rg, Material type, StateManager sm) {
		this(rg, type, sm, null);
	}
	protected CMRBlockHandler(RewardGroup rg, Material type, StateManager sm, Boolean growth) {
		this.type = type;
		this.rewardSections.add(rg.getName());
		this.gcm = GlobalConfigManager.getInstance();
		this.sm = sm;
		this.growth = growth;
	}
	public int execute(BlockState block, Player player, int globalRewardLimit) {
		CMRLogger.debug("Processing block " + block.getType().toString());
		int rewardsExecuted = 0;
		List<RewardGroup> groupCache = new ArrayList<>(CMRBlockManager.getInstance().getGroupCache());
		if (gcm.isRandomizingRewardOrder()) {
			Collections.shuffle(groupCache);
		}
		for (RewardGroup group : groupCache) {
			CMRLogger.debug("Processing group from cache: " + group.getName());
			if (rewardSections.contains(group.getName())) {
				CMRLogger.debug("Found group from cache: " + group.getName());
				rewardsExecuted += group.execute(block, player, globalRewardLimit - rewardsExecuted);
				if (globalRewardLimit > -1 && globalRewardLimit - rewardsExecuted < 1) break;
			}
		}
		uses++;
		return rewardsExecuted;
	}
	public boolean matches(BlockState state) {
		if (state.getType() != this.type) return false;
		return sm.matches(state, growth);
	}
	public CMRBlockState getBlockState() {
		return new CMRBlockState(this.getType(), this.getGrowth());
	}
	public Material getType() {
		return type;
	}
	public Boolean getGrowth() {
		return growth;
	}
	protected Set<String> getSections() {
		return rewardSections;
	}
	protected void addGroup(RewardGroup rg) {
		rewardSections.add(rg.getName());
	}
	protected void removeGroup(RewardGroup rg) {
		rewardSections.remove(rg.getName());
	}
	public long getUses() {
		return uses;
	}
	public boolean canHaveData() {
		return sm.canHaveData(type);
	}
	@Override
	public String toString() {
		String s = getType().toString();
		if (growth != null) {
			s += ":" + growth.toString();
		}
		return s;
	}
}
