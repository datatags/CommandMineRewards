package me.datatags.commandminerewards.gui.guis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.gui.GUIManager;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.buttons.area.AreaButton;
import me.datatags.commandminerewards.gui.buttons.general.FillerButton;

public abstract class AreaListGUI extends PaginatedGUI {
	protected RewardGroup group;
	protected List<AreaButton> buttons = new ArrayList<>();
	public AreaListGUI(RewardGroup group) {
		this.group = group;
		for (int i = 0; i < gui[0].length; i++) {
			gui[2][i] = new FillerButton();
			if (i % 4 != 0) { // silly way of skipping first, middle, and last slot 
				gui[5][i] = new FillerButton();
			}
		}
		addPageButtons();
	}
	@Override
	public CMRGUI getPreviousGUI() {
		GUIManager gm = getGUIManager();
		if (group == null) {
			return gm.getGUI(MainGUI.class, null, null);
		} else {
			return gm.getGUI(RewardGroupGUI.class, group, null);
		}
	}
	@Override
	public int getMaxPages() {
		SortedAreaButtons sab = new SortedAreaButtons(buttons);
		return (int) Math.ceil(Math.max(sab.inButtons.size(), sab.outButtons.size()) / 18d);
	}
	@Override
	public GUIButton[][] getPage(int pageN) {
		GUIButton[][] page = gui.clone();
		SortedAreaButtons sab = new SortedAreaButtons(buttons);
		int startIndex = (pageN - 1) * 18;
		for (int i = startIndex; i < pageN * 18; i++) {
			int row = (i - startIndex) / 9;
			if (i < sab.inButtons.size()) {
				page[row][i % 9] = sab.inButtons.get(i);
			}
			if (i < sab.outButtons.size()) {
				page[row][i % 9] = sab.outButtons.get(i);
			}
		}
		return page;
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
			title += "group: " + group.getName();
		}
		return title;
	}
	public abstract String getAreaType();
}
