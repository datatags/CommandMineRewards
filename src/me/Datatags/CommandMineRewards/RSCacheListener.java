package me.Datatags.CommandMineRewards;

public interface RSCacheListener {
	public default void registerCacheListener() {
		CMRBlockManager.getInstance().registerListener(this);
	}
	public default void reloadCache() {}
	public default void loadSection(RewardSection section) {}
	public default void unloadSection(String section) {}
	public default void reloadSection(RewardSection section) {}
	
	public default void loadReward(RewardSection section, Reward reward) {}
	public default void unloadReward(RewardSection section, String rewardName) {}
	public default void reloadReward(RewardSection section, Reward reward) {}
}
