package me.Datatags.CommandMineRewards;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CMRBlockManager {
	private List<CMRBlockHandler> handlers = new ArrayList<>();
	private CommandMineRewards cmr;
	private Set<RewardSection> rewardSectionCache = new HashSet<RewardSection>();
	private static CMRBlockManager instance;
	private static final int sortDelayTicks = 300*20;
	private GlobalConfigManager gcm;
	private CMRBlockManager(CommandMineRewards cmr) {
		this.cmr = cmr;
		this.gcm = GlobalConfigManager.getInstance();
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
		for (RewardSection rs : rewardSectionCache) {
			for (String blockName : rs.getRawBlocks()) {
				String[] segments = blockName.split(":", 2);
				Material mat = Material.matchMaterial(segments[0]);
				if (mat == null) {
					cmr.error("Invalid material " + segments[0] + " found when initializing handlers");
					continue;
				}
				if (segments.length == 1) {
					addHandler(rs, mat);
				} else { // must have two elements
					if (!(mat.createBlockData() instanceof Ageable)) {
						cmr.error("Type " + mat.toString() + " does not grow!");
					}
					if (segments[1].equalsIgnoreCase("true")) { // using if statements instead of Boolean.parse because if the user puts in garbage, Boolean.parse assumes false when we should notify the user and move on
						addCropHandler(rs, mat, true);
					} else if (segments[1].equalsIgnoreCase("false")) {
						addCropHandler(rs, mat, false);
					} else {
						cmr.error("Invalid growth identifier for material " + mat.toString() + ": " + segments[1]);
						cmr.error("Defaulting to any growth stage for " + mat.toString() + " in " + rs.getName());
						addHandler(rs, mat);
					}
				}
			}
		}
	}
	public void addHandler(RewardSection rs, Material type) {
		addCropHandler(rs, type, null);
	}
	public void removeHandler(RewardSection rs, Material type) {
		removeCropHandler(rs, type, null);
	}
	public void addCropHandler(RewardSection rs, Material type, Boolean growth) {
		debug("Adding handler for " + type);
		CMRBlockState state = new CMRBlockState(type, growth);
		CMRBlockHandler handler = getHandler(state);
		if (handler == null) {
			handlers.add(new CMRBlockHandler(rs, type, growth));
		} else {
			handler.addSection(rs);
		}
	}
	public void removeCropHandler(RewardSection rs, Material type, Boolean growth) {
		debug("Removing handler for " + type);
		CMRBlockState state = new CMRBlockState(type, growth);
		CMRBlockHandler handler = getHandler(state);
		if (handler == null) {
			cmr.warning("Attempted to remove an non-existant handler for " + type + ", " + growth + " in " + rs.getName());
			return;
		}
		if (handler.getSections().size() > 1) {
			handler.removeSection(rs);
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
				// we don't return here in case we have different crop states in different
				// reward sections, like one manages any stage of wheat while one only does full-grown wheat.
			} else {
				debug("Handler " + handler.getType() + " did not match block " + state.getType());
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
		rewardSectionCache.clear();
		for (RewardSection section : GlobalConfigManager.getInstance().getRewardSections()) {
			rewardSectionCache.add(section);
		}
		reloadHandlers();
	}
	public void reloadSection(String name) {
		unloadSection(name);
		loadSection(name);
	}
	public void loadSection(String name) {
		rewardSectionCache.add(new RewardSection(name, false));
	}
	public void unloadSection(String name) {
		cmr.debug("Reloading section " + name);
		RewardSection reloading = null;
		for (RewardSection section : rewardSectionCache) {
			if (section.getName().equals(name)) {
				reloading = section;
			}
		}
		if (reloading != null) {
			rewardSectionCache.remove(reloading);
		} else {
			cmr.warning("A section reload was requested but the section in question was not found!");
		}
	}
	public Set<RewardSection> getSectionCache() {
		return rewardSectionCache;
	}
}
