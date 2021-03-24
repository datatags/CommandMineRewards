package me.datatags.commandminerewards.gui.buttons.area;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.AreaAlreadyInListException;
import me.datatags.commandminerewards.Exceptions.AreaNotInListException;
import me.datatags.commandminerewards.Exceptions.InvalidAreaException;
import me.datatags.commandminerewards.gui.ItemBuilder;

public class WorldButton extends AreaButton {
	
	public WorldButton(String world, RewardGroup group) {
		super(world, group);
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.WORLD;
	}

	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.WORLD_MODIFY;
	}

	@Override
	protected ItemBuilder buildBase() {
		Material mat;
		ChatColor color;
		World realWorld = Bukkit.getWorld(area);
		// in case a non-existent world is in a list
		Environment env = realWorld == null ? null : realWorld.getEnvironment();
		if (env == null) {
			mat = Material.BARRIER;
			color = ChatColor.WHITE;
		} else if (env == Environment.NETHER) {
			mat = Material.NETHERRACK;
			color = ChatColor.RED;
		} else if (env == Environment.THE_END) {
			mat = Material.END_STONE;
			color = ChatColor.YELLOW;
		} else {
			mat = Material.GRASS_BLOCK;
			color = ChatColor.GREEN;
		}
		return new ItemBuilder(mat).name(color + area).lore(ChatColor.LIGHT_PURPLE + "Click to toggle");
	}
	
	@Override
	protected List<String> getAreas() {
		return group == null ? gcm.getGlobalAllowedWorlds() : group.getAllowedWorlds();
	}
	@Override
	protected void addGlobal() throws InvalidAreaException, AreaAlreadyInListException {
		gcm.addGlobalAllowedWorld(area);
	}
	@Override
	protected void addLocal() throws InvalidAreaException, AreaAlreadyInListException {
		group.addAllowedWorld(area);
	}
	@Override
	protected void removeGlobal() throws InvalidAreaException, AreaNotInListException {
		gcm.removeGlobalAllowedWorld(area);
	}
	@Override
	protected void removeLocal() throws InvalidAreaException, AreaNotInListException {
		group.removeAllowedWorld(area);
	}

}
