package me.Datatags.CommandMineRewards.gui.buttons.area;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.CommandMineRewards;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.Exceptions.AreaAlreadyInListException;
import me.Datatags.CommandMineRewards.Exceptions.AreaNotInListException;
import me.Datatags.CommandMineRewards.Exceptions.InvalidAreaException;
import me.Datatags.CommandMineRewards.gui.ItemBuilder;
import me.Datatags.CommandMineRewards.worldguard.WorldGuardManager;

public class RegionButton extends AreaButton {
	private WorldGuardManager wgm;
	public RegionButton(String area, RewardSection section) {
		super(area, section);
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
		section.addAllowedRegion(area);
	}

	@Override
	protected void removeGlobal() throws InvalidAreaException, AreaNotInListException {
		gcm.removeGlobalAllowedRegion(area);
	}

	@Override
	protected void removeLocal() throws InvalidAreaException, AreaNotInListException {
		section.removeAllowedRegion(area);
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
