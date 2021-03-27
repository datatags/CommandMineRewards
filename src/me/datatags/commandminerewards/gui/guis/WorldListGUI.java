package me.datatags.commandminerewards.gui.guis;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.buttons.area.WorldButton;

public class WorldListGUI extends AreaListGUI {
	public WorldListGUI(RewardGroup group) {
		super(group);
		Set<String> worlds = new HashSet<>(); 
		for (World world : Bukkit.getWorlds()) {
			worlds.add(world.getName().toLowerCase());
		}
		if (group == null) {
			for (String world : GlobalConfigManager.getInstance().getGlobalAllowedWorlds()) {
				worlds.add(world.toLowerCase());
			}
		} else {
			for (String world : group.getAllowedWorlds()) {
				worlds.add(world.toLowerCase());
			}
		}
		for (String world : worlds) {
			buttons.add(new WorldButton(world, group));
		}
	}
	
	@Override
	public WorldListGUI clone() {
		return new WorldListGUI(group);
	}

	@Override
	public String getAreaType() {
		return "World";
	}

}
