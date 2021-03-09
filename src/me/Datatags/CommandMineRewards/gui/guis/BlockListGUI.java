package me.Datatags.CommandMineRewards.gui.guis;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.Exceptions.BlockAlreadyInListException;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.buttons.misc.BlockButton;

public class BlockListGUI extends PaginatedGUI {
	private RewardSection section;
	private List<BlockButton> buttons;
	public BlockListGUI(RewardSection section) {
		this.section = section;
		for (Entry<String,Boolean> entry : section.getBlocksWithData().entrySet()) {
			buttons.add(new BlockButton(section, Material.matchMaterial(entry.getKey()), entry.getValue()));
		}
	}
	@Override
	public int getMaxPages() {
		return (int)Math.ceil(section.getBlocksWithData().size() / 54d);
	}

	@Override
	public GUIButton[][] getPage(int pageN) {
		GUIButton[][] page = gui.clone();
		Iterator<BlockButton> iter = buttons.iterator();
		for (int i = 0; i < gui.length; i++) {
			for (int j = 0; j < gui[0].length; j++) {
				if (iter.hasNext()) {
					gui[i][j] = iter.next();
				}
			}
		}
		return page;
	}

	@Override
	public String getTitle() {
		return ChatColor.GOLD + "Blocks list for section " + section.getName();
	}
	
	@Override
	public boolean skipFillers() {
		return true;
	}
	
	@Override
	public CMRGUI getPreviousGUI() {
		return getGUIManager().getGUI(RewardSectionGUI.class, section, null);
	}
	
	@Override
	public void onClick(InventoryClickEvent e) {
		if (e.isCancelled()) return; // idk why this would happen but just in case
		e.setCancelled(true); // cancel except in certain situations
		ClickType click = e.getClick();
		if (click.isKeyboardClick() || click == ClickType.MIDDLE || e.getClickedInventory() == null) {
			// keyboard clicks are hard to work with so ignore them
			// middle clicks don't actually drop blocks so ignore them
			// clicking outside the inventory doesn't help us in any way so ignore it
			return;
		}
		boolean topClicked = e.getClickedInventory().equals(e.getView().getTopInventory());
		ItemStack testItem = null;
		if (topClicked) {
			if (e.getCurrentItem() != null) return;
			testItem = e.getCursor();
		} else {
			testItem = e.getCurrentItem();
		}
		if (testItem == null || !testItem.getType().isBlock()) return;
		try {
			section.addBlock(testItem.getType());
		} catch (BlockAlreadyInListException ex) {
			return;
		}
		buttons.add(new BlockButton(section, testItem.getType(), null));
		e.setCancelled(false);
		this.refreshGUI((Player) e.getWhoClicked());
	}
}
