package me.datatags.commandminerewards;

public interface RGCacheListener {
	public default void registerCacheListener() {
		CMRBlockManager.getInstance().registerListener(this);
	}
	public default void onCacheReload() {}
	public default void onGroupLoad(RewardGroup group) {}
	public default void onGroupUnload(String group) {}
	public default void onGroupReload(RewardGroup group) {}
	
	public default void onRewardLoad(RewardGroup group, Reward reward) {}
	public default void onRewardUnload(RewardGroup group, String rewardName) {}
	public default void onRewardReload(RewardGroup group, Reward reward) {}
}
