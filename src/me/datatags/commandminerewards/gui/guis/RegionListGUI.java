package me.datatags.commandminerewards.gui.guis;

import java.util.List;

import me.datatags.commandminerewards.CommandMineRewards;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.buttons.area.RegionButton;

public class RegionListGUI extends AreaListGUI {
	public RegionListGUI(RewardGroup group) {
		super(group);
		for (String region : getRegions()) {
			buttons.add(new RegionButton(region, group));
		}
	}

	private List<String> getRegions() {
		return CommandMineRewards.getInstance().getWGManager().getAllRegions();
	}

	@Override
	public String getAreaType() {
		return "Region";
	}

}
