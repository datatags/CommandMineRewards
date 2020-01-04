package me.AlanZ.CommandMineRewards;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

public class CMRBlockHandler {
	private Material type;
	private Boolean growth = null;
	// we don't store actual reward sections since they're intended to be single-use objects and won't get updated if info changes
	private Set<String> rewardSections = new HashSet<String>();
	CMRBlockHandler(RewardSection rs, Material type) {
		this.type = type;
		this.rewardSections.add(rs.getName());
	}
	CMRBlockHandler(RewardSection rs, Material type, Boolean growth) {
		this(rs, type);
		this.growth = growth;
	}
	public void execute(Block block, Player player) {
		CMRBlockManager.cmr.debug("Processing block " + block.getType().toString());
		for (String sectionName : rewardSections) {
			new RewardSection(sectionName).execute(block, player);
		}
	}
	public boolean matches(Block block) {
		if (block.getType() != this.type) return false;
		if (this.growth == null) return true;
		Ageable cropData = (Ageable) block.getBlockData();
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
	Set<String> getSections() {
		return rewardSections;
	}
	void addSection(RewardSection rs) {
		rewardSections.add(rs.getName());
	}
	void removeSection(RewardSection rs) {
		rewardSections.remove(rs.getName());
	}
}
