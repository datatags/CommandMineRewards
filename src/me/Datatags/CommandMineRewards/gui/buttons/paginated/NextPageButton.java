package me.Datatags.CommandMineRewards.gui.buttons.paginated;

public class NextPageButton extends PageButton {
	@Override
	public int getPageOffset() {
		return 1;
	}
	@Override
	public String getItemName() {
		return "Next";
	}
}
