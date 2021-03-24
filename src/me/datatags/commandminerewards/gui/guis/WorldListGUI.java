package me.datatags.commandminerewards.gui.guis;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.buttons.area.WorldButton;

public class WorldListGUI extends AreaListGUI {
	public WorldListGUI(RewardGroup group) {
		super(group);
		for (World world : Bukkit.getWorlds()) {
			buttons.add(new WorldButton(world.getName(), group));
		}
	}

	@Override
	public String getAreaType() {
		return "World";
	}

}
