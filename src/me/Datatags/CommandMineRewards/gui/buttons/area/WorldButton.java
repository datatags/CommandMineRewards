package me.Datatags.CommandMineRewards.gui.buttons.area;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.Exceptions.InvalidAreaException;
import me.Datatags.CommandMineRewards.Exceptions.AreaAlreadyInListException;
import me.Datatags.CommandMineRewards.Exceptions.AreaNotInListException;
import me.Datatags.CommandMineRewards.gui.ItemBuilder;

public class WorldButton extends AreaButton {
	
	public WorldButton(String world, RewardSection section) {
		super(world, section);
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
		return section == null ? gcm.getGlobalAllowedWorlds() : section.getAllowedWorlds();
	}
	@Override
	protected void addGlobal() throws InvalidAreaException, AreaAlreadyInListException {
		gcm.addGlobalAllowedWorld(area);
	}
	@Override
	protected void addLocal() throws InvalidAreaException, AreaAlreadyInListException {
		section.addAllowedWorld(area);
	}
	@Override
	protected void removeGlobal() throws InvalidAreaException, AreaNotInListException {
		gcm.removeGlobalAllowedWorld(area);
	}
	@Override
	protected void removeLocal() throws InvalidAreaException, AreaNotInListException {
		section.removeAllowedWorld(area);
	}

}
