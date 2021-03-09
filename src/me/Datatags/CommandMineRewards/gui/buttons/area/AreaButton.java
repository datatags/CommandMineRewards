package me.Datatags.CommandMineRewards.gui.buttons.area;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.Datatags.CommandMineRewards.CommandMineRewards;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.Exceptions.InvalidAreaException;
import me.Datatags.CommandMineRewards.Exceptions.AreaAlreadyInListException;
import me.Datatags.CommandMineRewards.Exceptions.AreaNotInListException;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;

public abstract class AreaButton extends GUIButton implements Comparable<AreaButton> {
	protected String area;
	protected RewardSection section;
	protected GlobalConfigManager gcm;
	public AreaButton(String area, RewardSection section) {
		this.area = area;
		this.section = section;
		this.gcm = GlobalConfigManager.getInstance();
	}
	
	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		if (isInList()) {
			removeArea();
		} else {
			addArea();
		}
		parent.openFor(player);
	}
	
	protected abstract List<String> getAreas();
	protected abstract void addGlobal() throws InvalidAreaException, AreaAlreadyInListException;
	protected abstract void addLocal() throws InvalidAreaException, AreaAlreadyInListException;
	protected abstract void removeGlobal() throws InvalidAreaException, AreaNotInListException;
	protected abstract void removeLocal() throws InvalidAreaException, AreaNotInListException;
	
	protected void addArea() {
		try {
			if (section == null) {
				addGlobal();
			} else {
				addLocal();
			}
		} catch (InvalidAreaException | AreaAlreadyInListException e) {
			CommandMineRewards.getInstance().getLogger().warning("Attempted to add " + area + " where it already existed!");
		}
	}
	protected void removeArea() {
		try {
			if (section == null) {
				removeGlobal();
			} else {
				removeLocal();
			}
		} catch (InvalidAreaException | AreaNotInListException e) {
			CommandMineRewards.getInstance().getLogger().warning("Attempted to remove " + area + " from where it did not exist!");
		}
	}
	public boolean isInList() {
		return getAreas().contains(area);
	}
	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		return getBase().build();
	}

	@Override
	public int compareTo(AreaButton o) {
		return area.compareToIgnoreCase(o.area);
	}
	
}
