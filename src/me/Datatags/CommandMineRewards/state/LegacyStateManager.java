package me.Datatags.CommandMineRewards.state;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.BlockState;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.material.NetherWarts;

@SuppressWarnings("deprecation")
public class LegacyStateManager implements StateManager {

	@Override
	public boolean isFullGrown(BlockState state) {
		MaterialData data = state.getData();
		if (data instanceof NetherWarts) {
			return ((NetherWarts)data).getState() == NetherWartsState.RIPE;
		} else {
			return ((Crops)data).getState() == CropState.RIPE;
		}
	}

	@Override
	public boolean canHaveData(Material mat) {
		return Crops.class.isAssignableFrom(mat.getData()) || NetherWarts.class.isAssignableFrom(mat.getData());
	}

}
