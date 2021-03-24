package me.datatags.commandminerewards.state;

import org.bukkit.Material;
import org.bukkit.block.BlockState;

public interface StateManager {
	public default boolean matches(BlockState state, Boolean growth) {
		if (growth == null) return true;
		if (growth && isFullGrown(state)) return true;
		if (!growth && !isFullGrown(state)) return true;
		return false;
	}
	public boolean isFullGrown(BlockState state);
	public boolean canHaveData(Material mat);
}
