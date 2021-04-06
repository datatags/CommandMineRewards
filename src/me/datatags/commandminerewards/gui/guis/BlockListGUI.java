package me.datatags.commandminerewards.gui.guis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.Exceptions.BlockAlreadyInListException;
import me.datatags.commandminerewards.gui.buttons.general.FillerButton;
import me.datatags.commandminerewards.gui.buttons.misc.BlockButton;

public class BlockListGUI extends PaginatedGUI {
	private RewardGroup group;
	private List<BlockButton> buttons = new ArrayList<>();
	public BlockListGUI(RewardGroup group) {
		this.group = group;
		for (Entry<String,Boolean> entry : group.getBlocksWithData().entrySet()) {
			buttons.add(new BlockButton(group, Material.matchMaterial(entry.getKey()), entry.getValue()));
		}
		for (int i = 0; i < 9; i++) {
			if (i % 4 == 0) continue;
			gui[5][i] = new FillerButton();
		}
		addPageButtons();
	}
	
	@Override
	public BlockListGUI clone() {
		return new BlockListGUI(group);
	}
	
	@Override
	public int getMaxPages() {
		return (int)Math.max(1, Math.ceil(group.getBlocksWithData().size() / 45d));
	}

	@Override
	public void preparePage(int pageN) {
		generatePage(pageN, 0, 45, buttons);
	}

	@Override
	public String getTitle() {
		return ChatColor.GOLD + "Blocks list for group " + group.getName();
	}
	
	@Override
	public boolean skipFillers() {
		return true;
	}
	
	@Override
	public CMRGUI getPreviousGUI() {
		return new RewardGroupGUI(group);
	}
	
	@Override
	public void onClick(InventoryClickEvent e) {
		if (!doOwnActions(e)) {
			super.onClick(e);
		}
	}
	
	private boolean doOwnActions(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if (!CMRPermission.BLOCK_MODIFY.test(player)) return false; // back button may still be useful
		if (e.getClickedInventory() == null) return false;
		if (e.getClickedInventory().equals(e.getView().getTopInventory())) return false; // handled by the GUI buttons
		ItemStack testItem = e.getCurrentItem();
		if (testItem == null || !testItem.getType().isBlock()) return true;
		try {
			group.addBlock(testItem.getType());
		} catch (BlockAlreadyInListException ex) {
			return true; // well, we tried
		}
		refreshAll();
		return true;
	}
}
