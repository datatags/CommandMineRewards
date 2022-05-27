package me.datatags.commandminerewards.gui.guis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.buttons.area.RegionButton;

public class RegionListGUI extends AreaListGUI {
	public RegionListGUI(RewardGroup group) {
		super(group);
		Set<String> regions = new HashSet<>();
		Set<String> allRegions = new HashSet<>();
		for (String region : getRegions()) {
			allRegions.add(region.toLowerCase());
		}
		regions.addAll(allRegions);
		if (group == null) {
			for (String region : GlobalConfigManager.getInstance().getGlobalAllowedRegions()) {
				regions.add(region.toLowerCase());
			}
		} else {
			for (String region : group.getAllowedRegions()) {
				regions.add(region.toLowerCase());
			}
		}
		for (String region : regions) {
			buttons.add(new RegionButton(region, group, allRegions.contains(region)));
		}
	}
	@Override
	public RegionListGUI getNewSelf() {
		return new RegionListGUI(group);
	}
	private List<String> getRegions() {
		return CommandMineRewards.getInstance().getWGManager().getAllRegions();
	}

	@Override
	public String getAreaType() {
		return "Region";
	}

}
