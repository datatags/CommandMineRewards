package me.datatags.commandminerewards.state;

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

import me.datatags.commandminerewards.CommandMineRewards;

@SuppressWarnings("deprecation")
public class LegacyStateManager implements StateManager {
	private boolean is18; 
	public LegacyStateManager() {
		is18 = CommandMineRewards.getInstance().getMinecraftVersion() < 9;
	}
	private static final Set<Material> CROPS_18 = new HashSet<>(Arrays.asList(Material.POTATO, Material.CARROT, Material.valueOf("CROPS"), Material.valueOf("NETHER_WARTS")));
	@Override
	public boolean isFullGrown(BlockState state) {
		MaterialData data = state.getData();
		if (data instanceof Crops) {
			return ((Crops)data).getState() == CropState.RIPE;
		} else if (data instanceof NetherWarts) {
			return ((NetherWarts)data).getState() == NetherWartsState.RIPE;
		}
		return is18 && state.getRawData() == 7; // seems to be the only way of checking for fully-grown carrots/potatoes in 1.8
	}

	@Override
	public boolean canHaveData(Material mat) {
		if (is18) {
			return CROPS_18.contains(mat);
		} else {
			Class<? extends MaterialData> type = mat.getData();
			return Crops.class.isAssignableFrom(type) || NetherWarts.class.isAssignableFrom(type);
		}
	}

}
