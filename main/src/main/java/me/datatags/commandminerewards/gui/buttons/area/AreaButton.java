package me.datatags.commandminerewards.gui.buttons.area;

import java.util.List;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.AreaAlreadyInListException;
import me.datatags.commandminerewards.Exceptions.AreaNotInListException;
import me.datatags.commandminerewards.Exceptions.InvalidAreaException;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public abstract class AreaButton extends GUIButton implements Comparable<AreaButton> {
    protected String area;
    protected RewardGroup group;
    protected GlobalConfigManager gcm;
    public AreaButton(String area, RewardGroup group) {
        this.area = area;
        this.group = group;
        this.gcm = GlobalConfigManager.getInstance();
    }

    @Override
    public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {
        if (isInList()) {
            removeArea();
        } else {
            addArea();
        }
        holder.updateGUI();
    }
    
    protected abstract List<String> getAreas();
    protected abstract void addGlobal() throws InvalidAreaException, AreaAlreadyInListException;
    protected abstract void addLocal() throws InvalidAreaException, AreaAlreadyInListException;
    protected abstract void removeGlobal() throws InvalidAreaException, AreaNotInListException;
    protected abstract void removeLocal() throws InvalidAreaException, AreaNotInListException;
    
    protected void addArea() {
        try {
            if (group == null) {
                addGlobal();
            } else {
                addLocal();
            }
        } catch (InvalidAreaException | AreaAlreadyInListException e) {
            CMRLogger.warning("Attempted to add " + area + " where it already existed!");
        }
    }
    protected void removeArea() {
        try {
            if (group == null) {
                removeGlobal();
            } else {
                removeLocal();
            }
        } catch (InvalidAreaException | AreaNotInListException e) {
            CMRLogger.warning("Attempted to remove " + area + " from where it did not exist!");
        }
    }
    public boolean isInList() {
        return getAreas().contains(area);
    }

    @Override
    public int compareTo(AreaButton o) {
        return area.compareToIgnoreCase(o.area);
    }
    
}
