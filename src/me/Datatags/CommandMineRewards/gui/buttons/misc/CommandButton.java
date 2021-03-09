package me.Datatags.CommandMineRewards.gui.buttons.misc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.CommandMineRewards;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.Reward;
import me.Datatags.CommandMineRewards.Exceptions.CommandNotInListException;
import me.Datatags.CommandMineRewards.commands.RewardCommandEntry;
import me.Datatags.CommandMineRewards.gui.ItemBuilder;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.conversations.InsertCommandPrompt;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;

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
	protected ItemBuilder buildBase() {
		ItemBuilder ib = new ItemBuilder(Material.PAPER).name(entry.getCommand());
		ib.lore(ChatColor.GREEN + "Click to insert after");
		ib.lore(ChatColor.GREEN + "Shift-click to insert before");
		ib.lore(ChatColor.RED + "Right-click to delete");
		return ib;
	}

	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		return getBase().build();
	}

	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		if (clickType.isLeftClick()) {
			int index = reward.getCommands().indexOf(entry);
			if (index == -1) {
				CommandMineRewards.getInstance().getLogger().warning("Couldn't find index of clicked command button");
			}
			if (!clickType.isShiftClick()) {
				index++;
			}
			parent.getGUIManager().startConversation(player, new InsertCommandPrompt(reward, index));
		} else if (clickType.isRightClick()) {
			try {
				reward.removeCommand(entry.getCommand());
			} catch (CommandNotInListException e) {
				e.printStackTrace();
				return;
			}
			parent.refreshGUI(player);
		}
	}
	
	public String getCommand() {
		return entry.getCommand();
	}
}
