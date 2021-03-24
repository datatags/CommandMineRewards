package me.Datatags.CommandMineRewards;

public interface RGCacheListener {
	public default void registerCacheListener() {
		CMRBlockManager.getInstance().registerListener(this);
	}
	public default void reloadCache() {}
	public default void loadGroup(RewardGroup group) {}
	public default void unloadSection(String group) {}
	public default void reloadGroup(RewardGroup group) {}
	
	public default void loadReward(RewardGroup group, Reward reward) {}
	public default void unloadReward(RewardGroup group, String rewardName) {}
	public default void reloadReward(RewardGroup group, Reward reward) {}
}
