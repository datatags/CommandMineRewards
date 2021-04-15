package me.datatags.commandminerewards.gui.buttons.general;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.datatags.commandminerewards.CMRPermission;
import me.datatags.commandminerewards.GlobalConfigManager;
import me.datatags.commandminerewards.Reward;
import me.datatags.commandminerewards.RewardGroup;
import me.datatags.commandminerewards.SilkTouchPolicy;
import me.datatags.commandminerewards.gui.GUIUserHolder;
import me.datatags.commandminerewards.gui.ItemBuilder;
import me.datatags.commandminerewards.gui.buttons.GUIButton;
import me.datatags.commandminerewards.gui.guis.CMRGUI;

public class SilkTouchButton extends GUIButton {
	private RewardGroup rewardGroup;
	private Reward reward;
	private GlobalConfigManager gcm;
	public SilkTouchButton(RewardGroup rewardGroup, Reward reward) {
		this.rewardGroup = rewardGroup;
		this.reward = reward;
		this.gcm = GlobalConfigManager.getInstance();
	}
		
	@Override
	protected ItemBuilder build() {
		ItemBuilder ib = new ItemBuilder(Material.ENCHANTED_BOOK).name(ChatColor.LIGHT_PURPLE + "Silk Touch Policy");
		SilkTouchPolicy local = getLocalPolicy();
		SilkTouchPolicy inherits = getInheritablePolicy();
		ib.lore("Local value: " + local.getFriendlyName()).lore("Can inherit: " + inherits.getFriendlyName());
		return ib;
	}
	
	@Override
	public void addClickableLore(Player player) {
		base.lore(ChatColor.DARK_PURPLE + "Left click to cycle through policies");
		base.lore(ChatColor.DARK_PURPLE + "Right click to inherit policy");
	}
	
	private SilkTouchPolicy getInheritablePolicy() {
		if (rewardGroup == null) {
			return SilkTouchPolicy.IGNORED;
		} else if (reward == null) {
			return gcm.getSilkTouchPolicy(null, null);
		} else {
			return gcm.getSilkTouchPolicy(rewardGroup, null);
		}
	}
	
	private SilkTouchPolicy getLocalPolicy() {
		if (rewardGroup == null) {
			return gcm.getGlobalSilkTouchPolicy();
		} else if (reward == null) {
			return rewardGroup.getSilkTouchPolicy();
		} else {
			return reward.getSilkTouchPolicy();
		}
	}
	
	private void setLocalPolicy(SilkTouchPolicy stp) {
		if (rewardGroup == null) {
			gcm.setGlobalSilkTouchPolicy(stp);
		} else if (reward == null) {
			rewardGroup.setSilkTouchPolicy(stp);
		} else {
			reward.setSilkTouchPolicy(stp);
		}
	}

	@Override
	public void onClick(GUIUserHolder holder, ItemStack is, CMRGUI parent, ClickType clickType) {
		SilkTouchPolicy next;
		if (clickType.isLeftClick()) {
			next = nextPolicy(getLocalPolicy());
		} else if (clickType.isRightClick()) {
			next = SilkTouchPolicy.INHERIT;
		} else {
			return;
		}
		setLocalPolicy(next);
		holder.updateGUI();
	}
	
	private SilkTouchPolicy nextPolicy(SilkTouchPolicy stp) {
		switch (stp) {
		case REQUIRED:
			return SilkTouchPolicy.IGNORED;
		case IGNORED:
			return SilkTouchPolicy.DISALLOWED;
		case DISALLOWED:
		default: // INHERIT or anything else that's weird
			return SilkTouchPolicy.REQUIRED;
		}
	}

	@Override
	public CMRPermission getPermission() {
		return CMRPermission.SILKTOUCHPOLICY;
	}
	
	@Override
	public CMRPermission getClickPermission() {
		return CMRPermission.SILKTOUCHPOLICY_MODIFY;
	}
	
}
