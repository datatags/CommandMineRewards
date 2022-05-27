package me.datatags.commandminerewards.gui.buttons.area;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.AreaAlreadyInListException;
import me.datatags.commandminerewards.Exceptions.AreaNotInListException;
import me.datatags.commandminerewards.Exceptions.InvalidAreaException;
import me.datatags.commandminerewards.gui.ItemBuilder;

public class RegionButton extends AreaButton {
    private boolean isRealRegion;
    public RegionButton(String area, RewardGroup group, boolean isRealRegion) {
        super(area, group);
        this.isRealRegion = isRealRegion;
    }

    @Override
    protected List<String> getAreas() {
        return group == null ? gcm.getGlobalAllowedRegions() : group.getAllowedRegions();
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
    protected ItemBuilder build() {
        Material mat;
        ChatColor color;
        String friendlyArea = area;
        if (area.equals("*")) {
            mat = Material.NETHER_STAR;
            color = ChatColor.LIGHT_PURPLE;
            friendlyArea += " (all)";
        } else if (isRealRegion) {
            mat = Material.STRUCTURE_BLOCK;
            color = ChatColor.GREEN;
        } else {
            mat = Material.BARRIER;
            color = ChatColor.RED;
        }
        ItemBuilder ib = new ItemBuilder(mat).name(color + friendlyArea);
        if (!isRealRegion) {
            if (!area.equals("*")) {
                ib.lore(ChatColor.RED + "Region does not exist");
            }
            ib.lore(ChatColor.RED + "Click to remove");
        } else {
            ib.lore(ChatColor.LIGHT_PURPLE + "Click to toggle");
        }
        return ib;
    }

}
