package me.datatags.commandminerewards;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.datatags.commandminerewards.state.StateManager;

public class CMRBlockHandler {
    private final CMRBlockState state;
    private final GlobalConfigManager gcm;
    private final StateManager sm;
    private Map<String,Double> rewardSections = new HashMap<>();
    private long uses = 0;

    protected CMRBlockHandler(RewardGroup rg, CMRBlockState state, StateManager sm) {
        this.state = state;
        this.gcm = GlobalConfigManager.getInstance();
        this.sm = sm;
        this.rewardSections.put(rg.getName(), state.getMultiplier());
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
            if (rewardSections.containsKey(group.getName())) {
                CMRLogger.debug("Found group from cache: " + group.getName());
                rewardsExecuted += group.execute(block, player, state.getMultiplier(), globalRewardLimit - rewardsExecuted);
                if (globalRewardLimit > -1 && globalRewardLimit - rewardsExecuted < 1) break;
            }
        }
        uses++;
        return rewardsExecuted;
    }

    public boolean matches(BlockState state) {
        if (state.getType() != this.state.getType()) return false;
        return sm.matches(state, this.state.getGrowth());
    }

    public CMRBlockState getBlockState() {
        return state;
    }

    protected Set<String> getSections() {
        return rewardSections.keySet();
    }

    protected void addGroup(RewardGroup rg, double multiplier) {
        rewardSections.put(rg.getName(), multiplier);
    }

    protected void removeGroup(RewardGroup rg) {
        rewardSections.remove(rg.getName());
    }

    public long getUses() {
        return uses;
    }

    public boolean canHaveData() {
        return sm.canHaveData(state.getType());
    }

    @Override
    public String toString() {
        String s = state.getType().toString();
        if (state.getGrowth() != null) {
            s += ":" + state.getGrowth().toString();
        }
        return s;
    }
}
