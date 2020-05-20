package me.Datatags.CommandMineRewards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

public class CMRBlockHandler {
	private Material type;
	private Boolean growth = null;
	private Set<String> rewardSections = new HashSet<String>();
	private long uses = 0;
	private GlobalConfigManager gcm;
	protected CMRBlockHandler(RewardSection rs, Material type) {
		this.type = type;
		this.rewardSections.add(rs.getName());
		this.gcm = GlobalConfigManager.getInstance();
	}
	protected CMRBlockHandler(RewardSection rs, Material type, Boolean growth) {
		this(rs, type);
		this.growth = growth;
	}
	public int execute(BlockState block, Player player, int globalRewardLimit) {
		CommandMineRewards cmr = CommandMineRewards.getInstance();
		cmr.debug("Processing block " + block.getType().toString());
		int rewardsExecuted = 0;
		List<RewardSection> sectionCache = new ArrayList<>(CMRBlockManager.getInstance().getSectionCache());
		if (gcm.isRandomizingRewardOrder()) {
			Collections.shuffle(sectionCache);
		}
		for (RewardSection section : sectionCache) {
			cmr.debug("Processing section from cache: " + section.getName());
			if (rewardSections.contains(section.getName())) {
				cmr.debug("Found section from cache: " + section.getName());
				rewardsExecuted += section.execute(block, player, globalRewardLimit - rewardsExecuted);
				if (globalRewardLimit > -1 && globalRewardLimit - rewardsExecuted < 1) break;
			}
		}
		uses++;
		return rewardsExecuted;
	}
	public boolean matches(BlockState state) {
		if (state.getType() != this.type) return false;
		if (this.growth == null) return true;
		Ageable cropData = (Ageable) state.getBlockData();
		if (this.growth && cropData.getAge() == cropData.getMaximumAge()) return true;
		if (!this.growth && cropData.getAge() < cropData.getMaximumAge()) return true;
		return false;
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
	protected void addSection(RewardSection rs) {
		rewardSections.add(rs.getName());
	}
	protected void removeSection(RewardSection rs) {
		rewardSections.remove(rs.getName());
	}
	public long getUses() {
		return uses;
	}
}
