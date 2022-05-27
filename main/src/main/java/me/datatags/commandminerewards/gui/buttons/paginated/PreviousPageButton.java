package me.datatags.commandminerewards.gui.buttons.paginated;

public class PreviousPageButton extends PageButton {

    @Override
    public int getPageOffset() {
        return -1;
    }

    @Override
    public String getItemName() {
        return "Previous";
    }

}
