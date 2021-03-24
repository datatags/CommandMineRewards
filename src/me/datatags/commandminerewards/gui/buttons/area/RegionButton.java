package me.datatags.commandminerewards.gui.buttons.area;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.AreaAlreadyInListException;
import me.datatags.commandminerewards.Exceptions.AreaNotInListException;
import me.datatags.commandminerewards.Exceptions.InvalidAreaException;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.worldguard.WorldGuardManager;

public class RegionButton extends AreaButton {
	private WorldGuardManager wgm;
	public RegionButton(String area, RewardGroup group) {
		super(area, group);
		this.wgm = CommandMineRewards.getInstance().getWGManager();
	}

	@Override
	protected List<String> getAreas() {
		return wgm.getAllRegions();
	}

	@Override
	protected void addGlobal() throws InvalidAreaException, AreaAlreadyInListException {
		gcm.addGlobalAllowedRegion(area);
	}

	@Override
	protected void addLocal() throws InvalidAreaException, AreaAlreadyInListException {
		group.addAllowedRegion(area);
	}

	@Override
	protected void removeGlobal() throws InvalidAreaException, AreaNotInListException {
		gcm.removeGlobalAllowedRegion(area);
	}

	@Override
	protected void removeLocal() throws InvalidAreaException, AreaNotInListException {
		group.removeAllowedRegion(area);
	}

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.REGION;
	}

	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.REGION_MODIFY;
	}

	@Override
	protected ItemBuilder buildBase() {
		return new ItemBuilder(Material.STRUCTURE_BLOCK).name(ChatColor.GREEN + area).lore(ChatColor.LIGHT_PURPLE + "Click to toggle");
	}

}
