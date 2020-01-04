package me.AlanZ.CommandMineRewards;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

public class CMRBlockManager {
	private static Set<CMRBlockHandler> handlers = new HashSet<>();
	static CommandMineRewards cmr;
	public static void initializeHandlers(CommandMineRewards cmr) {
		CMRBlockManager.cmr = cmr;
		for (RewardSection rs : GlobalConfigManager.getRewardSections()) {
			for (String blockName : rs.getRawBlocks()) {
				String[] segments = blockName.split(":", 2);
				Material mat = Material.matchMaterial(segments[0]);
				if (mat == null) {
					cmr.getLogger().severe("Invalid material " + segments[0]);
					continue;
				}
				if (segments.length == 1) {
					addHandler(rs, mat);
				} else { // must have two elements
					if (!(mat.createBlockData() instanceof Ageable)) {
						cmr.getLogger().severe("Type " + mat.toString() + " does not grow!");
					}
					if (segments[1].equalsIgnoreCase("true")) { // using if statements instead of Boolean.parse because if the user puts in garbage, Boolean.parse assumes false when we should notify the user and move on
						addCropHandler(rs, mat, true);
					} else if (segments[1].equalsIgnoreCase("false")) {
						addCropHandler(rs, mat, false);
					} else {
						cmr.getLogger().severe("Invalid growth identifier for material " + mat.toString() + ": " + segments[1]);
						cmr.getLogger().severe("Defaulting to any growth stage for " + mat.toString() + " in " + rs.getName());
						addHandler(rs, mat);
					}
				}
			}
		}
	}
	public static void addHandler(RewardSection rs, Material type) {
		addCropHandler(rs, type, null);
	}
	public static void removeHandler(RewardSection rs, Material type) {
		removeCropHandler(rs, type, null);
	}
	public static void addCropHandler(RewardSection rs, Material type, Boolean growth) {
		CMRBlockState state = new CMRBlockState(type, growth);
		CMRBlockHandler handler = getHandler(state);
		if (handler == null) {
			handlers.add(new CMRBlockHandler(rs, type, growth));
		} else {
			handler.addSection(rs);
		}
	}
	public static void removeCropHandler(RewardSection rs, Material type, Boolean growth) {
		CMRBlockState state = new CMRBlockState(type, growth);
		CMRBlockHandler handler = getHandler(state);
		if (handler == null) {
			cmr.getLogger().warning("Attempted to remove an non-existant handler for " + type + ", " + growth + " in " + rs.getName());
			return;
		}
		if (handler.getSections().size() < 2) {
			handlers.remove(handler);
		} else {
			handler.removeSection(rs);
		}
	}
	private static void debug(String msg) {
		cmr.debug(msg);
	}
	public static void executeAllSections(Block block, Player player) {
		debug("----------START REWARD CALCS----------");
		for (CMRBlockHandler handler : handlers) {
			if (handler.matches(block)) {
				handler.execute(block, player);
			}
		}
		debug("----------END REWARD CALCS----------");
	}
	public static CMRBlockHandler getHandler(CMRBlockState state) {
		for (CMRBlockHandler handler : handlers) {
			if (handler.getBlockState().equals(state)) return handler;
		}
		return null;
	}
}
