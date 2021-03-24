package me.Datatags.CommandMineRewards.gui.guis;

import java.util.List;

import me.Datatags.CommandMineRewards.CommandMineRewards;
import me.Datatags.CommandMineRewards.RewardGroup;
import me.Datatags.CommandMineRewards.gui.buttons.area.RegionButton;

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
