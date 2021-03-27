package me.datatags.commandminerewards.gui.guis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.buttons.area.AreaButton;
import me.datatags.commandminerewards.gui.buttons.general.FillerButton;

public abstract class AreaListGUI extends PaginatedGUI {
	protected RewardGroup group;
	protected List<AreaButton> buttons = new ArrayList<>();
	public AreaListGUI(RewardGroup group) {
		this.group = group;
		for (int i = 0; i < gui[0].length; i++) {
			gui[2][i] = new FillerButton();
			if (i % 4 != 0) { // skip first, middle, and last slot 
				gui[5][i] = new FillerButton();
			}
		}
		addPageButtons();
	}
	@Override
	public boolean skipFillers() {
		return true;
	}
	@Override
	public CMRGUI getPreviousGUI() {
		if (group == null) {
			return new MainGUI();
		} else {
			return new RewardGroupGUI(group);
		}
	}
	@Override
	public int getMaxPages() {
		SortedAreaButtons sab = new SortedAreaButtons(buttons);
		return (int) Math.ceil(Math.max(sab.inButtons.size(), sab.outButtons.size()) / 18d);
	}
	@Override
	public void preparePage(int pageN) {
		SortedAreaButtons sab = new SortedAreaButtons(buttons);
		int startIndex = (pageN - 1) * 18;
		for (int i = startIndex; i < pageN * 18; i++) {
			int row = (i - startIndex) / 9;
			if (i < sab.inButtons.size()) {
				gui[row][i % 9] = sab.inButtons.get(i);
			}
			if (i < sab.outButtons.size()) {
				gui[row + 3][i % 9] = sab.outButtons.get(i);
			}
		}
	}
	private class SortedAreaButtons {
		private List<AreaButton> inButtons = new ArrayList<>();
		private List<AreaButton> outButtons = new ArrayList<>();
		public SortedAreaButtons(List<AreaButton> buttons) {
			for (AreaButton button : buttons) {
				if (button.isInList()) {
					inButtons.add(button);
				} else {
					outButtons.add(button);
				}
			}
			Collections.sort(inButtons);
			Collections.sort(outButtons);
		}
	}
	@Override
	public String getTitle() {
		String title = "Allowed " + getAreaType() + " - ";
		if (group == null) {
			title += "Global";
		} else {
			title += "Group: " + group.getName();
		}
		return title;
	}
	public abstract String getAreaType();
}
