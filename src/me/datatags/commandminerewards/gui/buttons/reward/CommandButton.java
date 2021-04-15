package me.datatags.commandminerewards.gui.buttons.reward;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRLogger;
import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.Exceptions.CommandNotInListException;
import me.datatags.commandminerewards.commands.RewardCommandEntry;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.conversations.CMRConversationFactory;
import me.datatags.commandminerewards.gui.conversations.InsertCommandPrompt;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class CommandButton extends GUIButton {
	private Reward reward;
	private RewardCommandEntry entry;
	public CommandButton(Reward reward, RewardCommandEntry entry) {
		this.reward = reward;
		this.entry = entry;
	}
	@Override
	public CMRPermission getPermission() {
		return CMRPermission.COMMAND;
	}

	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.COMMAND_MODIFY;
	}

	@Override
	protected ItemBuilder build() {
		ItemBuilder ib;
		if (entry == null) {
			ib = new ItemBuilder(Material.WRITABLE_BOOK).name(ChatColor.RED + "No commands");
			ib.lore(ChatColor.GREEN + "Click to add");
		} else {
			ib = new ItemBuilder(Material.PAPER).name(entry.getCommand());
		}
		return ib;
	}
	
	@Override
	public void addClickableLore(Player player) {
		base.lore(ChatColor.GREEN + "Click to insert after");
		base.lore(ChatColor.GREEN + "Shift-click to insert before");
		base.lore(ChatColor.RED + "Right-click to delete");
	}

	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		if (clickType.isLeftClick()) {
			int index;
			if (entry == null) {
				index = 0;
			} else {
				index = reward.getCommands().indexOf(entry);
				if (index == -1) {
					CMRLogger.warning("Couldn't find index of clicked command button");
					return;
				}
				if (!clickType.isShiftClick()) {
					index++;
				}
			}
			CMRConversationFactory.startConversation(player, new InsertCommandPrompt(reward, index));
		} else if (clickType.isRightClick()) {
			try {
				reward.removeCommand(entry.getCommand());
			} catch (CommandNotInListException e) {
				e.printStackTrace();
				return;
			}
			CMRGUI.refreshAll();
		}
	}
	
	public String getCommand() {
		return entry.getCommand();
	}
}
