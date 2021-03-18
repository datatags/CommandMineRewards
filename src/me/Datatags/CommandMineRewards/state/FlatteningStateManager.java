package me.Datatags.CommandMineRewards.state;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;

public class FlatteningStateManager implements StateManager {
	@Override
	public boolean isFullGrown(BlockState state) {
		Ageable cropData = (Ageable) state.getBlockData();
		return cropData.getMaximumAge() == cropData.getAge();
	}

	@Override
	public boolean canHaveData(Material mat) {
		return mat.createBlockData() instanceof Ageable;
	}
	
}
