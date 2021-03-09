package me.Datatags.CommandMineRewards.gui.guis;

import java.util.List;

import me.Datatags.CommandMineRewards.CommandMineRewards;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.gui.buttons.area.RegionButton;

public class RegionListGUI extends AreaListGUI {
	public RegionListGUI(RewardSection section) {
		super(section);
		for (String region : getRegions()) {
			buttons.add(new RegionButton(region, section));
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
