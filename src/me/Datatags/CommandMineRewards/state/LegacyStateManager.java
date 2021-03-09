package me.Datatags.CommandMineRewards.state;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.material.Crops;

@SuppressWarnings("deprecation")
public class LegacyStateManager implements StateManager {

	@Override
	public boolean isFullGrown(BlockState state) {
		return ((Crops)state.getData()).getState() == CropState.RIPE;
	}

	@Override
	public boolean canHaveData(Material mat) {
		return Crops.class.isAssignableFrom(mat.getData());
	}

}
