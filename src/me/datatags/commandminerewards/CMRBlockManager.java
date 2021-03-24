package me.datatags.commandminerewards;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.datatags.commandminerewards.state.FlatteningStateManager;
import me.datatags.commandminerewards.state.LegacyStateManager;
import me.datatags.commandminerewards.state.StateManager;

public class CMRBlockManager {
	private List<CMRBlockHandler> handlers = new ArrayList<>();
	private CommandMineRewards cmr;
	private Set<RewardGroup> rewardGroupCache = new HashSet<RewardGroup>();
	private static CMRBlockManager instance;
	private static final int sortDelayTicks = 300*20;
	private GlobalConfigManager gcm;
	private Set<RGCacheListener> listeners = new HashSet<>();
	private StateManager sm;
	private CMRBlockManager(CommandMineRewards cmr) {
		this.cmr = cmr;
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
				cmr.debug("Running cleanup");
				handlers.sort(Comparator.comparing(CMRBlockHandler::getUses).reversed());
			}
		}.runTaskTimer(cmr, sortDelayTicks, sortDelayTicks);
	}
	public static CMRBlockManager getInstance() {
		if (instance == null) {
			instance = new CMRBlockManager(CommandMineRewards.getInstance());
			instance.reloadCache(); // after init so we don't have a StackOverflowException
		}
		return instance;
	}
	private void reloadHandlers() {
		handlers.clear();
		for (RewardGroup rg : rewardGroupCache) {
			for (String blockName : rg.getRawBlocks()) {
				String[] segments = blockName.split(":", 2);
				Material mat = Material.matchMaterial(segments[0]);
				if (mat == null) {
					cmr.error("Invalid material " + segments[0] + " found when initializing handlers");
					continue;
				}
				if (segments.length == 1) {
					addHandler(rg, mat);
				} else { // must have two elements
					if (!sm.canHaveData(mat)) {
						cmr.error("Type " + mat.toString() + " does not grow!");
					}
					if (segments[1].equalsIgnoreCase("true")) { // using if statements instead of Boolean.parse because if the user puts in garbage, Boolean.parse assumes false when we should notify the user and move on
						addCropHandler(rg, mat, true);
					} else if (segments[1].equalsIgnoreCase("false")) {
						addCropHandler(rg, mat, false);
					} else {
						cmr.error("Invalid growth identifier for material " + mat.toString() + ": " + segments[1]);
						cmr.error("Defaulting to any growth stage for " + mat.toString() + " in " + rg.getName());
						addHandler(rg, mat);
					}
				}
			}
		}
	}
	public void addHandler(RewardGroup rg, Material type) {
		addCropHandler(rg, type, null);
	}
	public void removeHandler(RewardGroup rg, Material type) {
		removeCropHandler(rg, type, null);
	}
	public void addCropHandler(RewardGroup rg, Material type, Boolean growth) {
		debug("Adding handler for " + type);
		CMRBlockState state = new CMRBlockState(type, growth);
		CMRBlockHandler handler = getHandler(state);
		if (handler == null) {
			handlers.add(new CMRBlockHandler(rg, type, sm, growth));
		} else {
			handler.addGroup(rg);
		}
	}
	public void removeCropHandler(RewardGroup rg, Material type, Boolean growth) {
		debug("Removing handler for " + type);
		CMRBlockState state = new CMRBlockState(type, growth);
		CMRBlockHandler handler = getHandler(state);
		if (handler == null) {
			cmr.warning("Attempted to remove an non-existant handler for " + type + ", " + growth + " in " + rg.getName());
			return;
		}
		if (handler.getSections().size() > 1) {
			handler.removeGroup(rg);
		} else {
			handlers.remove(handler);
		}
	}
	private void debug(String msg) {
		cmr.debug(msg);
	}
	public void executeAllSections(BlockState state, Player player) {
		debug("----------START REWARD CALCS----------");
		debug("If nothing is listed here, no handlers were found.");
		debug("Total available handlers: " + handlers.size());
		int globalRewardLimit = gcm.getGlobalRewardLimit();
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
		debug("-----------END REWARD CALCS-----------");
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
			listener.reloadCache();
		}
	}
	public void reloadSection(String name) {
		unloadSection(name, true);
		RewardGroup group = loadGroup(name, true);
		for (RGCacheListener listener : listeners) {
			listener.reloadGroup(group);
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
				listener.loadGroup(group);
			}
		}
		return group;
	}
	public void unloadSection(String name) {
		unloadSection(name, false);
	}
	public void unloadSection(String name, boolean reload) {
		cmr.debug("Reloading group " + name);
		RewardGroup reloading = null;
		for (RewardGroup group : rewardGroupCache) {
			if (group.getName().equals(name)) {
				reloading = group;
			}
		}
		if (reloading != null) {
			rewardGroupCache.remove(reloading);
		} else {
			cmr.warning("A group reload was requested but the group in question was not found!");
		}
		if (!reload) {
			for (RGCacheListener listener : listeners) {
				listener.unloadSection(name);
			}
		}
	}
	public Set<RewardGroup> getGroupCache() {
		return rewardGroupCache;
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
