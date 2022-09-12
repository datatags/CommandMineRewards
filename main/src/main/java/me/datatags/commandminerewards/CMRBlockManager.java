package me.datatags.commandminerewards;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.datatags.commandminerewards.state.FlatteningStateManager;
import me.datatags.commandminerewards.state.LegacyStateManager;
import me.datatags.commandminerewards.state.StateManager;

public class CMRBlockManager {
    private static final String HEADER = "----------START REWARD CALCS----------";
    private static final String FOOTER = "-----------END REWARD CALCS-----------";
    private List<CMRBlockHandler> handlers = new ArrayList<>();
    private Set<RewardGroup> rewardGroupCache = new HashSet<>();
    private static CMRBlockManager instance;
    private static final int sortDelayTicks = 300 * 20;
    private GlobalConfigManager gcm;
    private Set<RGCacheListener> listeners = new HashSet<>();
    private StateManager sm;

    private CMRBlockManager() {
        this.gcm = GlobalConfigManager.getInstance();
        if (CommandMineRewards.getInstance().isLegacyMinecraft()) {
            sm = new LegacyStateManager();
        } else {
            sm = new FlatteningStateManager();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                // do a bit of optimizing
                // don't do this async
                CMRLogger.debug("Running cleanup");
                handlers.sort(Comparator.comparing(CMRBlockHandler::getUses).reversed());
            }
        }.runTaskTimer(CommandMineRewards.getInstance(), sortDelayTicks, sortDelayTicks);
    }

    public static CMRBlockManager getInstance() {
        if (instance == null) {
            instance = new CMRBlockManager();
            instance.reloadCache(); // after init so we don't have a StackOverflowException
        }
        return instance;
    }

    private void reloadHandlers() {
        handlers.clear();
        for (RewardGroup rg : rewardGroupCache) {
            for (CMRBlockState state : rg.getBlocks()) {
                addCropHandler(rg, state);
            }
        }
    }

    public void addCropHandler(RewardGroup rg, CMRBlockState state) {
        debug("Adding handler for " + state);
        CMRBlockHandler handler = getHandler(state);
        if (handler == null) {
            handlers.add(new CMRBlockHandler(rg, state, sm));
        } else {
            handler.addGroup(rg, state.getMultiplier());
        }
    }

    public void removeCropHandler(RewardGroup rg, CMRBlockState state) {
        debug("Removing handler for " + state);
        CMRBlockHandler handler = getHandler(state);
        if (handler == null) {
            CMRLogger.warning("Attempted to remove an non-existent handler for " + state + " in " + rg.getName());
            return;
        }
        if (handler.getSections().size() > 1) {
            handler.removeGroup(rg);
        } else {
            handlers.remove(handler);
        }
    }

    private void debug(String msg) {
        CMRLogger.debug(msg);
    }

    public void executeAllGroups(BlockState state, Player player) {
        debug(HEADER);
        debug("If nothing is listed here, no handlers were found.");
        debug("Total available handlers: " + handlers.size());
        int globalRewardLimit = gcm.getGlobalRewardLimit();
        if (globalRewardLimit == 0) {
            debug("Skipping all handlers, global reward limit is 0");
            debug(FOOTER);
            return;
        }
        int rewardsExecuted = 0;
        for (CMRBlockHandler handler : handlers) {
            if (handler.matches(state)) {
                rewardsExecuted += handler.execute(state, player, globalRewardLimit - rewardsExecuted);
                if (globalRewardLimit > -1 && globalRewardLimit - rewardsExecuted < 1) {
                    debug("Hit global reward limit, quitting");
                    break;
                }
                if (!handler.canHaveData()) {
                    break;
                }
            } else {
                debug("Handler " + handler.toString() + " did not match block " + state.getType());
            }
        }
        debug(FOOTER);
    }

    public CMRBlockHandler getHandler(CMRBlockState state) {
        for (CMRBlockHandler handler : handlers) {
            if (handler.getBlockState().equals(state)) return handler;
        }
        return null;
    }

    public void reloadCache() {
        rewardGroupCache.clear();
        for (RewardGroup group : GlobalConfigManager.getInstance().getRewardGroups()) {
            rewardGroupCache.add(group);
        }
        reloadHandlers();
        for (RGCacheListener listener : listeners) {
            listener.onCacheReload();
        }
    }

    public void reloadGroup(String name) {
        unloadGroup(name, true);
        RewardGroup group = loadGroup(name, true);
        for (RGCacheListener listener : listeners) {
            listener.onGroupReload(group);
        }
    }

    public RewardGroup loadGroup(String name) {
        return loadGroup(name, false);
    }

    public RewardGroup loadGroup(String name, boolean reload) {
        RewardGroup group = new RewardGroup(name, false);
        rewardGroupCache.add(group);
        if (!reload) {
            for (RGCacheListener listener : listeners) {
                listener.onGroupLoad(group);
            }
        }
        return group;
    }

    public void removeHandlers(String section) {
        RewardGroup group = getGroup(section);
        Iterator<CMRBlockHandler> iter = handlers.iterator();
        while (iter.hasNext()) {
            CMRBlockHandler handler = iter.next();
            if (handler.getSections().size() > 1) {
                handler.removeGroup(group);
            } else {
                iter.remove();
            }
        }
    }

    public void unloadGroup(String name) {
        unloadGroup(name, false);
    }

    public void unloadGroup(String name, boolean reload) {
        CMRLogger.debug("Reloading group " + name);
        RewardGroup reloading = null;
        for (RewardGroup group : rewardGroupCache) {
            if (group.getName().equals(name)) {
                reloading = group;
            }
        }
        if (reloading != null) {
            rewardGroupCache.remove(reloading);
        } else {
            CMRLogger.warning("A group reload was requested but the group in question was not found!");
        }
        if (!reload) {
            for (RGCacheListener listener : listeners) {
                listener.onGroupUnload(name);
            }
        }
    }

    public void reloadReward(RewardGroup parent, Reward reward) {
        CMRLogger.debug("Reloading reward " + reward.getName());
        for (RGCacheListener listener : listeners) {
            listener.onRewardReload(parent, reward);
        }
    }

    public Reward loadReward(RewardGroup parent, Reward reward) {
        CMRLogger.debug("Loading reward " + reward.getName());
        for (RGCacheListener listener : listeners) {
            listener.onRewardLoad(parent, reward);
        }
        return reward;
    }

    public void unloadReward(RewardGroup parent, String name) {
        CMRLogger.debug("Unloading reward " + name);
        for (RGCacheListener listener : listeners) {
            listener.onRewardUnload(parent, name);
        }
    }

    public Set<RewardGroup> getGroupCache() {
        return rewardGroupCache;
    }

    public RewardGroup getGroup(String name) {
        for (RewardGroup group : rewardGroupCache) {
            if (group.getName().equalsIgnoreCase(name)) return group;
        }
        return null;
    }

    public void registerListener(RGCacheListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(RGCacheListener listener) {
        listeners.remove(listener);
    }

    public StateManager getStateManager() {
        return sm;
    }
}
