package me.Datatags.CommandMineRewards.gui.guis;

import java.util.List;

import org.bukkit.entity.Player;

import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.buttons.general.FillerButton;
import me.Datatags.CommandMineRewards.gui.buttons.paginated.NextPageButton;
import me.Datatags.CommandMineRewards.gui.buttons.paginated.PageButton;
import me.Datatags.CommandMineRewards.gui.buttons.paginated.PreviousPageButton;

public abstract class PaginatedGUI extends CMRGUI {
	@Override
	public void openFor(Player player) {
		openFor(player, 1);
	}
	public void openFor(Player player, int page) {
		if (page > getMaxPages()) {
			throw new IllegalArgumentException("Max pages is " + getMaxPages() + ", got " + page);
		}
		GUIButton[][] toOpen = getPage(page);
		for (GUIButton[] column : toOpen) {
			for (GUIButton button : column) {
				if (button instanceof PageButton) {
					if (getMaxPages() == 1
							|| (page == 1 && button instanceof PreviousPageButton)
							|| (page == getMaxPages() && button instanceof NextPageButton)) {
						button = new FillerButton();
					} else {
						((PageButton)button).setPageTag(page);
					}
				}
			}
		}
		player.openInventory(generateInventory(player, toOpen));
	}
	public abstract int getMaxPages();
	public abstract GUIButton[][] getPage(int pageN);
	protected void addPageButtons() { // adds page buttons in default spots
		int lastRow = gui.length - 1;
		gui[lastRow][0] = new PreviousPageButton();
		gui[lastRow][8] = new NextPageButton();
	}
	protected GUIButton[][] generatePage(int pageN, int startRow, int itemsPerPage, List<? extends GUIButton> buttons) {
		GUIButton[][] page = gui.clone();
		for (int i = (pageN - 1) * itemsPerPage; i < pageN * itemsPerPage && i < buttons.size(); i++) { // don't go over page limit or group cache size
			page[(i / 9) + startRow][i % 9] = buttons.get(i);
		}
		return page;
	}
}
