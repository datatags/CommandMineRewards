package me.datatags.commandminerewards.gui.guis;

import java.util.List;

import org.bukkit.entity.Player;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.buttons.general.FillerButton;
import me.datatags.commandminerewards.gui.buttons.paginated.NextPageButton;
import me.datatags.commandminerewards.gui.buttons.paginated.PageButton;
import me.datatags.commandminerewards.gui.buttons.paginated.PreviousPageButton;

public abstract class PaginatedGUI extends CMRGUI {
	protected int recentPage;
	@Override
	public void openFor(Player player) {
		openFor(player, 1);
	}
	public void openFor(Player player, int page) {
		openFor(gm.getNewHolder(player, this), page);
	}
	@Override
	public void openFor(GUIUserHolder holder) {
		openFor(holder, 1);
	}
	public void openFor(GUIUserHolder holder, int page) {
		if (page < 1) {
			throw new IllegalArgumentException("Min page is 1, got " + page);
		}
		if (page > getMaxPages()) {
			throw new IllegalArgumentException("Max page is " + getMaxPages() + ", got " + page);
		}
		recentPage = page;
		preparePage(page);
		for (int y = 0; y < gui.length; y++) {
			for (int x = 0; x < gui[0].length; x++) {
				GUIButton button = gui[y][x];
				if (!(button instanceof PageButton)) continue;
				if (getMaxPages() == 1
						|| (page == 1 && button instanceof PreviousPageButton)
						|| (page == getMaxPages() && button instanceof NextPageButton)) {
					gui[y][x] = new FillerButton();
				} else {
					((PageButton)button).setPage(page);
				}
			}
		}
		super.openFor(holder);
	}
	public abstract int getMaxPages();
	public abstract void preparePage(int pageN);
	protected void addPageButtons() { // adds page buttons in default spots
		int lastRow = gui.length - 1;
		gui[lastRow][0] = new PreviousPageButton();
		gui[lastRow][8] = new NextPageButton();
	}
	protected void generatePage(int pageN, int startRow, int itemsPerPage, List<? extends GUIButton> buttons) {
		for (int i = (pageN - 1) * itemsPerPage; i < pageN * itemsPerPage && i < buttons.size(); i++) { // don't go over page limit or group cache size
			gui[((i % itemsPerPage) / 9) + startRow][i % 9] = buttons.get(i);
		}
	}
}
