package me.Datatags.CommandMineRewards.gui.guis;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.gui.buttons.area.WorldButton;

public class WorldListGUI extends AreaListGUI {
	public WorldListGUI(RewardSection section) {
		super(section);
		for (World world : Bukkit.getWorlds()) {
			buttons.add(new WorldButton(world.getName(), section));
		}
	}

	@Override
	public String getAreaType() {
		return "World";
	}

}
