package me.Datatags.CommandMineRewards.state;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.BlockState;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.material.NetherWarts;

@SuppressWarnings("deprecation")
public class LegacyStateManager implements StateManager {
	private static final Set<Material> CROPS = new HashSet<>(Arrays.asList(Material.POTATO, Material.CARROT, Material.valueOf("CROPS"), Material.valueOf("NETHER_WARTS")));
	@Override
	public boolean isFullGrown(BlockState state) {
		MaterialData data = state.getData();
		if (data instanceof Crops) {
			return ((Crops)data).getState() == CropState.RIPE;
		} else if (data instanceof NetherWarts) {
			return ((NetherWarts)data).getState() == NetherWartsState.RIPE;
		}
		return state.getRawData() == 7; // seems to be the only way of checking for fully-grown carrots/potatoes in 1.8
	}

	@Override
	public boolean canHaveData(Material mat) {
		return CROPS.contains(mat);
	}

}
