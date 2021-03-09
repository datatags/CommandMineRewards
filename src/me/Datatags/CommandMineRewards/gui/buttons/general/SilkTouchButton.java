package me.Datatags.CommandMineRewards.gui.buttons.general;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import me.Datatags.CommandMineRewards.CMRPermission;
import me.Datatags.CommandMineRewards.GlobalConfigManager;
import me.Datatags.CommandMineRewards.Reward;
import me.Datatags.CommandMineRewards.RewardSection;
import me.Datatags.CommandMineRewards.SilkTouchPolicy;
import me.Datatags.CommandMineRewards.gui.ItemBuilder;
import me.Datatags.CommandMineRewards.gui.buttons.GUIButton;
import me.Datatags.CommandMineRewards.gui.guis.CMRGUI;

public class SilkTouchButton extends GUIButton {
	private RewardSection rewardSection;
	private Reward reward;
	public SilkTouchButton(RewardSection rewardSection, Reward reward) {
		this.rewardSection = rewardSection;
		this.reward = reward;
	}
		
	@Override
	protected ItemBuilder buildBase() {
		ItemBuilder ib = new ItemBuilder(Material.ENCHANTED_BOOK).name(ChatColor.LIGHT_PURPLE + "Silk Touch Policy");
		ib.lore(ChatColor.DARK_PURPLE + "Left click to cycle through policies");
		ib.lore(ChatColor.DARK_PURPLE + "Right click to inherit policy");
		return ib;
	}
	
	@Override
	protected ItemStack personalize(Player player, GlobalConfigManager gcm) {
		SilkTouchPolicy local = getLocalPolicy(gcm);
		SilkTouchPolicy inherits;
		if (rewardSection == null) {
			inherits = SilkTouchPolicy.IGNORED;
		} else {
			inherits = gcm.getSilkTouchPolicy(rewardSection, reward);
		}
		return getBase().lore("Local value: " + local.getFriendlyName()).lore("Can inherit: " + inherits.getFriendlyName()).build();
	}
	
	private SilkTouchPolicy getLocalPolicy(GlobalConfigManager gcm) {
		if (rewardSection == null) {
			return gcm.getGlobalSilkTouchPolicy();
		} else {
			if (reward == null) {
				return rewardSection.getSilkTouchPolicy();
			} else {
				return reward.getSilkTouchPolicy();
			}
		}
	}
	
	private void setLocalPolicy(SilkTouchPolicy stp, GlobalConfigManager gcm) {
		if (rewardSection == null) {
			gcm.setGlobalSilkTouchPolicy(stp);
		} else {
			if (reward == null) {
				rewardSection.setSilkTouchPolicy(stp);
			} else {
				reward.setSilkTouchPolicy(stp);
			}
		}
	}

	@Override
	public void onClick(Player player, ItemStack is, CMRGUI parent, ClickType clickType) {
		GlobalConfigManager gcm = GlobalConfigManager.getInstance();
		SilkTouchPolicy next;
		if (clickType.isLeftClick()) {
			next = nextPolicy(getLocalPolicy(gcm));
		} else if (clickType.isRightClick()) {
			next = SilkTouchPolicy.INHERIT;
		} else {
			return;
		}
		setLocalPolicy(next, gcm);
		parent.openFor(player);
	}
	
	private SilkTouchPolicy nextPolicy(SilkTouchPolicy stp) {
		switch (stp) {
		case REQUIRED:
			return SilkTouchPolicy.IGNORED;
		case IGNORED:
			return SilkTouchPolicy.DISALLOWED;
		case DISALLOWED:
		default: // INHERIT
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
